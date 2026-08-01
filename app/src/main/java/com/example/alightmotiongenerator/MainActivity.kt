package com.example.alightmotiongenerator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
            if (email.isEmpty() || !email.contains("@")) {
                showToast("Masukkan email yang valid!")
                return@setOnClickListener
            }
            sendMagicLink(email)
        }

        // Verify Account
        binding.btnVerify.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val rawLink = binding.etRawLink.text.toString().trim()

            if (email.isEmpty() || !email.contains("@")) {
                showToast("Masukkan email yang valid!")
                return@setOnClickListener
            }
            if (rawLink.isEmpty() || !rawLink.startsWith("http")) {
                showToast("Paste raw link yang valid!")
                return@setOnClickListener
            }
            verifyAccount(email, rawLink)
        }

        // Copy Result
        binding.btnCopyResult.setOnClickListener {
            if (lastResultJson.isNotEmpty()) {
                copyToClipboard(lastResultJson)
                showToast("✅ Hasil berhasil disalin!")
            } else {
                showToast("Tidak ada hasil untuk disalin")
            }
        }

        // Clear
        binding.btnClear.setOnClickListener {
            binding.etEmail.text?.clear()
            binding.etRawLink.text?.clear()
            binding.tvResult.text = "Hasil akan muncul di sini..."
            lastResultJson = ""
            showToast("Form dikosongkan")
        }
    }

    private fun sendMagicLink(email: String) {
        setLoading(true, "send")
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
                            "Paste link di kolom Raw Link lalu tekan Verifikasi"
                        ))
                    }
                    updateResult(result.toString(2))
                    showToast("✅ Magic link berhasil dikirim!")
                } else {
                    handleError(response.errorBody()?.string() ?: "Gagal mengirim link")
                }
            } catch (e: Exception) {
                handleError(e.message ?: "Terjadi kesalahan jaringan")
            } finally {
                setLoading(false, "send")
            }
        }
    }

    private fun verifyAccount(email: String, rawLink: String) {
        setLoading(true, "verify")
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
                    showToast("🎉 Akun Premium berhasil diaktifkan!")
                } else {
                    handleError(response.errorBody()?.string() ?: "Gagal verifikasi")
                }
            } catch (e: Exception) {
                handleError(e.message ?: "Terjadi kesalahan jaringan")
            } finally {
                setLoading(false, "verify")
            }
        }
    }

    private fun handleError(errorMessage: String) {
        val result = JSONObject().apply {
            put("success", false)
            put("error", errorMessage)
        }
        updateResult(result.toString(2))
        showToast("❌ Error: $errorMessage")
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

    private fun setLoading(isLoading: Boolean, type: String) {
        if (type == "send") {
            binding.btnSendLink.isEnabled = !isLoading
            binding.btnSendLink.text = if (isLoading) "MENGIRIM..." else "KIRIM MAGIC LINK"
        } else {
            binding.btnVerify.isEnabled = !isLoading
            binding.btnVerify.text = if (isLoading) "MEMVERIFIKASI..." else "VERIFIKASI & AKTIFKAN PREMIUM"
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}