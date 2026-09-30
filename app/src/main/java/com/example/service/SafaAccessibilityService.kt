package com.example.service

import android.accessibilityservice.AccessibilityService
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.example.SafaApplication
import com.example.data.model.LogLevel
import java.util.LinkedList

class SafaAccessibilityService : AccessibilityService() {

    companion object {
        var instance: SafaAccessibilityService? = null
            private set

        val isServiceRunning: Boolean
            get() = instance != null
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        log(LogLevel.SUCCESS, "ACCESSIBILITY", "Layanan Aksesibilitas SAFA AI TERHUBUNG. Siap memindai layar eksternal.")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Optional tracking of active window changes
    }

    override fun onInterrupt() {
        log(LogLevel.WARN, "ACCESSIBILITY", "Layanan Aksesibilitas diinterupsi sistem.")
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
        log(LogLevel.WARN, "ACCESSIBILITY", "Layanan Aksesibilitas terputus.")
    }

    /**
     * Memindai seluruh teks yang terlihat pada layar aplikasi aktif (Web / Browser / APK lain)
     */
    fun scanScreenText(): String {
        val rootNode = rootInActiveWindow ?: return ""
        val sb = StringBuilder()
        val queue = LinkedList<AccessibilityNodeInfo>()
        queue.add(rootNode)

        val seenTexts = mutableSetOf<String>()

        while (queue.isNotEmpty()) {
            val node = queue.poll() ?: continue

            val text = node.text?.toString()?.trim()
            val desc = node.contentDescription?.toString()?.trim()

            if (!text.isNullOrBlank() && seenTexts.add(text)) {
                sb.append(text).append("\n")
            } else if (!desc.isNullOrBlank() && seenTexts.add(desc)) {
                sb.append(desc).append("\n")
            }

            for (i in 0 until node.childCount) {
                node.getChild(i)?.let { queue.add(it) }
            }
        }

        return sb.toString().trim()
    }

    /**
     * Mencari opsi jawaban yang cocok pada aplikasi lain dan melakukan klik otomatis (Auto-Click)
     */
    fun autoClickMatchingOption(targetAnswer: String): Boolean {
        val rootNode = rootInActiveWindow ?: return false
        val cleanTarget = targetAnswer.replace("*", "").trim()
        val queue = LinkedList<AccessibilityNodeInfo>()
        queue.add(rootNode)

        var clicked = false

        while (queue.isNotEmpty()) {
            val node = queue.poll() ?: continue

            val nodeText = node.text?.toString()?.trim() ?: ""
            val nodeDesc = node.contentDescription?.toString()?.trim() ?: ""

            // Check if matches target option or letter prefix
            val matches = isMatch(nodeText, cleanTarget) || isMatch(nodeDesc, cleanTarget)

            if (matches) {
                // Attempt to click this node or nearest clickable parent
                var targetToClick: AccessibilityNodeInfo? = node
                while (targetToClick != null && !targetToClick.isClickable) {
                    targetToClick = targetToClick.parent
                }

                if (targetToClick != null && targetToClick.isClickable) {
                    targetToClick.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    clicked = true
                    log(LogLevel.SUCCESS, "AUTO_CLICK", "Berhasil klik elemen: '${nodeText.ifBlank { nodeDesc }}'")
                    break
                }
            }

            for (i in 0 until node.childCount) {
                node.getChild(i)?.let { queue.add(it) }
            }
        }

        return clicked
    }

    /**
     * Mengisi kolom input teks (EditText) yang sedang aktif di aplikasi lain
     */
    fun autoFillActiveInput(textToFill: String): Boolean {
        val rootNode = rootInActiveWindow ?: return false
        val queue = LinkedList<AccessibilityNodeInfo>()
        queue.add(rootNode)

        var filled = false

        while (queue.isNotEmpty()) {
            val node = queue.poll() ?: continue

            if (node.isEditable) {
                val arguments = Bundle().apply {
                    putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, textToFill)
                }
                node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
                filled = true
                log(LogLevel.SUCCESS, "AUTO_INJECT", "Berhasil menyuntikkan teks jawaban ke kolom input eksternal.")
                break
            }

            for (i in 0 until node.childCount) {
                node.getChild(i)?.let { queue.add(it) }
            }
        }

        return filled
    }

    private fun isMatch(source: String, target: String): Boolean {
        if (source.isBlank() || target.isBlank()) return false
        if (source.equals(target, ignoreCase = true)) return true
        if (target.startsWith(source, ignoreCase = true) || source.startsWith(target, ignoreCase = true)) return true

        // Match option prefix e.g. "A." or "B)"
        val prefix = target.take(2).trim()
        if (prefix.isNotEmpty() && source.startsWith(prefix, ignoreCase = true)) return true

        return false
    }

    private fun log(level: LogLevel, tag: String, message: String) {
        val app = applicationContext as? SafaApplication ?: return
        app.questionRepository.writeLog(level, tag, message)
    }
}
