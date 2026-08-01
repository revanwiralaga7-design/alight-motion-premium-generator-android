package com.example.alightmotiongenerator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.alightmotiongenerator.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var lastResultJson: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {
        // Send Magic Link
        binding.btnSendLink.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Email wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            sendMagicLink(email)
        }

        // Verify Account
        binding.btnVerify.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val rawLink = binding.etRawLink.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Email wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (rawLink.isEmpty()) {
                Toast.makeText(this, "Raw Link wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            verifyAccount(email, rawLink)
        }

        // Copy Result
        binding.btnCopyResult.setOnClickListener {
            if (lastResultJson.isNotEmpty()) {
                copyToClipboard(lastResultJson)
                Toast.makeText(this, "Hasil disalin ke clipboard!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Tidak ada hasil untuk disalin", Toast.LENGTH_SHORT).show()
            }
        }

        // Clear
        binding.btnClear.setOnClickListener {
            binding.etEmail.text?.clear()
            binding.etRawLink.text?.clear()
            binding.tvResult.text = "Hasil akan muncul di sini..."
            lastResultJson = ""
        }
    }

    private fun sendMagicLink(email: String) {
        showLoading(true)
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.sendMagicLink(SendRequest(email))

                if (response.isSuccessful) {
                    val body = response.body()
                    val result = JSONObject().apply {
                        put("success", true)
                        put("email", email)
                        put("message", body?.message ?: "Link berhasil dikirim")
                        put("orderCode", body?.orderCode ?: "")
                        put("instructions", listOf(
                            "Buka inbox email (cek folder Spam juga)",
                            "Cari email dari \"Alight Motion\" / \"Alight Creative\"",
                            "Tekan-tahan tombol \"Login ke Alight Creative\", pilih \"Salin URL\"",
                            "Jangan klik langsung — copy link doang",
                            "Paste link di kolom Raw Link lalu tekan Verifikasi"
                        ))
                    }
                    updateResult(result.toString(2))
                    Toast.makeText(this@MainActivity, "Magic link berhasil dikirim!", Toast.LENGTH_LONG).show()
                } else {
                    handleError(response.errorBody()?.string() ?: "Gagal mengirim link")
                }
            } catch (e: Exception) {
                handleError(e.message ?: "Terjadi kesalahan")
            } finally {
                showLoading(false)
            }
        }
    }

    private fun verifyAccount(email: String, rawLink: String) {
        showLoading(true)
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.verifyAccount(VerifyRequest(email, rawLink))

                if (response.isSuccessful) {
                    val body = response.body()
                    val result = JSONObject().apply {
                        put("success", true)
                        put("email", email)
                        put("message", body?.message ?: "Account verified successfully")
                        put("oobCode", body?.oobCode ?: "")
                        put("idToken", body?.idToken ?: "")
                        put("userProfile", body?.userProfile ?: JSONObject.NULL)
                        put("premium", true)
                        put("duration", "1 Tahun")
                    }
                    updateResult(result.toString(2))
                    Toast.makeText(this@MainActivity, "Akun berhasil diverifikasi!", Toast.LENGTH_LONG).show()
                } else {
                    handleError(response.errorBody()?.string() ?: "Gagal verifikasi")
                }
            } catch (e: Exception) {
                handleError(e.message ?: "Terjadi kesalahan")
            } finally {
                showLoading(false)
            }
        }
    }

    private fun handleError(errorMessage: String) {
        val result = JSONObject().apply {
            put("success", false)
            put("error", errorMessage)
        }
        updateResult(result.toString(2))
        Toast.makeText(this, "Error: $errorMessage", Toast.LENGTH_LONG).show()
    }

    private fun updateResult(json: String) {
        lastResultJson = json
        binding.tvResult.text = json
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Alight Result", text)
        clipboard.setPrimaryClip(clip)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.btnSendLink.isEnabled = !isLoading
        binding.btnVerify.isEnabled = !isLoading
        binding.btnCopyResult.isEnabled = !isLoading
        binding.btnClear.isEnabled = !isLoading

        if (isLoading) {
            binding.btnSendLink.text = "Mengirim..."
            binding.btnVerify.text = "Memverifikasi..."
        } else {
            binding.btnSendLink.text = getString(R.string.send_magic_link)
            binding.btnVerify.text = getString(R.string.verify_account)
        }
    }
}