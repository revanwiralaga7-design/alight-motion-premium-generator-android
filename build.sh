#!/bin/bash

echo "🚀 Building Alight Motion Premium Generator APK..."

# Download Gradle Wrapper if not exists
if [ ! -f "gradle/wrapper/gradle-wrapper.jar" ]; then
    echo "📥 Downloading Gradle Wrapper..."
    mkdir -p gradle/wrapper
    curl -sL https://raw.githubusercontent.com/gradle/gradle/v8.7.0/gradle/wrapper/gradle-wrapper.jar -o gradle/wrapper/gradle-wrapper.jar
fi

# Make gradlew executable
chmod +x gradlew 2>/dev/null || true

echo "🔨 Building Debug APK..."
./gradlew assembleDebug --no-daemon

if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    echo "📦 APK location: app/build/outputs/apk/debug/app-debug.apk"
    
    # Copy APK to root for easy access
    cp app/build/outputs/apk/debug/app-debug.apk ./AlightMotionGenerator-debug.apk 2>/dev/null || true
    
    echo ""
    echo "📱 Ready to install!"
    echo "   File: AlightMotionGenerator-debug.apk"
else
    echo "❌ Build failed. Please check the logs above."
    exit 1
fi