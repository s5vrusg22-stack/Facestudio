#!/usr/bin/env bash
set -euo pipefail
if ! command -v gradle >/dev/null 2>&1; then echo "Gradle 9.6.0이 필요합니다." >&2; exit 1; fi
if [ -z "$(printenv ANDROID_HOME)" ] && [ -z "$(printenv ANDROID_SDK_ROOT)" ]; then echo "ANDROID_HOME 또는 ANDROID_SDK_ROOT를 설정하세요." >&2; exit 1; fi
gradle :app:assembleDebug
echo "APK: app/build/outputs/apk/debug/app-debug.apk"
