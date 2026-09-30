package com.example.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.BuildConfig
import com.example.data.local.UserEntity
import com.example.data.model.LogLevel
import com.example.data.remote.GeminiApiClient
import com.example.data.remote.GeminiContent
import com.example.data.remote.GeminiGenConfig
import com.example.data.remote.GeminiPart
import com.example.data.remote.GeminiRequest
import com.example.data.repository.QuestionRepository
import com.example.ui.components.NeoBadge
import com.example.ui.components.NeoButton
import com.example.ui.components.NeoCard
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun CbtBrowserScreen(
    currentUser: UserEntity?,
    questionRepository: QuestionRepository,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    var urlInput by remember { mutableStateOf("https://www.google.com") }
    var currentUrl by remember { mutableStateOf("https://www.google.com") }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var progress by remember { mutableFloatStateOf(0f) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var webStatusText by remember { mutableStateOf("[READY] Browser CBT Aktif") }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
    ) {
        // Top Toolbar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyberBorder)
                .background(Color(0xFF090E17))
                .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Address bar
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .border(1.dp, CyberBorder, RoundedCornerShape(6.dp))
                            .background(CyberSurface, RoundedCornerShape(6.dp))
                            .padding(horizontal = 10.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        BasicTextField(
                            value = urlInput,
                            onValueChange = { urlInput = it },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                            keyboardActions = KeyboardActions(
                                onGo = {
                                    val formatted = if (urlInput.startsWith("http://") || urlInput.startsWith("https://")) {
                                        urlInput
                                    } else {
                                        "https://$urlInput"
                                    }
                                    currentUrl = formatted
                                    webViewInstance?.loadUrl(formatted)
                                }
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = TextWhite,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            ),
                            cursorBrush = SolidColor(NeonCyan),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { webViewInstance?.reload() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reload",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Auto-Fill Web Action Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NeoButton(
                        text = if (isAnalyzing) "[MEMINDAI WEB...]" else "[PINDAI WEB & AUTO-FILL]",
                        onClick = {
                            if (isAnalyzing) return@NeoButton
                            isAnalyzing = true
                            webStatusText = "[SCANNING] Mengekstrak teks DOM dari halaman web..."
                            questionRepository.writeLog(LogLevel.INFO, "WEB_CBT", "Mengekstrak seluruh teks dan elemen kuis pada halaman web...")

                            // JavaScript extraction script
                            val jsExtract = """
                                (function() {
                                    return document.body.innerText;
                                })();
                            """.trimIndent()

                            webViewInstance?.evaluateJavascript(jsExtract) { rawText ->
                                val cleanedText = rawText?.replace("\\n", "\n")?.replace("\"", "") ?: ""
                                if (cleanedText.isBlank() || cleanedText.length < 10) {
                                    isAnalyzing = false
                                    webStatusText = "[KOSONG] Tidak ada konten soal terdeteksi di web."
                                    return@evaluateJavascript
                                }

                                coroutineScope.launch(Dispatchers.IO) {
                                    try {
                                        val apiKey = currentUser?.geminiApiKey?.ifBlank { null }
                                            ?: BuildConfig.GEMINI_API_KEY

                                        val prompt = "Teks halaman web kuis/ujian:\n\n$cleanedText\n\n" +
                                                "Tugasmu:\n" +
                                                "1. Temukan soal aktif pada teks web di atas.\n" +
                                                "2. Tentukan jawaban yang paling tepat (huruf opsi A/B/C/D atau kalimat jawaban).\n" +
                                                "Format balasan WAJIB:\n" +
                                                "JAWABAN: <jawaban atau huruf opsi>"

                                        val resp = GeminiApiClient.api.generateContent(
                                            model = "gemini-3.5-flash",
                                            apiKey = apiKey,
                                            request = GeminiRequest(
                                                contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt)))),
                                                generationConfig = GeminiGenConfig(temperature = 0.1f)
                                            )
                                        )

                                        val resultText = resp.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
                                        var detectedAnswer = ""
                                        for (line in resultText.lines()) {
                                            if (line.trim().startsWith("JAWABAN:", ignoreCase = true)) {
                                                detectedAnswer = line.substringAfter(":").trim()
                                            }
                                        }
                                        if (detectedAnswer.isBlank()) {
                                            detectedAnswer = resultText.take(40).trim()
                                        }

                                        questionRepository.writeLog(LogLevel.SUCCESS, "WEB_CBT", "AI Menemukan Solusi: '$detectedAnswer'. Menyuntikkan DOM Click...")

                                        // JavaScript Injection to click radio / input matching the answer
                                        withContext(Dispatchers.Main) {
                                            val jsInject = """
                                                (function() {
                                                    var target = "$detectedAnswer".toLowerCase().replace(/[*`]/g, '');
                                                    var radios = document.querySelectorAll('input[type="radio"], input[type="checkbox"], label, [role="radio"]');
                                                    var clicked = false;
                                                    for (var i = 0; i < radios.length; i++) {
                                                        var el = radios[i];
                                                        var txt = (el.innerText || el.value || el.parentElement.innerText || '').toLowerCase();
                                                        if (txt.includes(target) || target.includes(txt.trim())) {
                                                            el.click();
                                                            clicked = true;
                                                            break;
                                                        }
                                                    }
                                                    return clicked;
                                                })();
                                            """.trimIndent()

                                            webViewInstance?.evaluateJavascript(jsInject) { jsResult ->
                                                isAnalyzing = false
                                                webStatusText = "[TERISI] Jawaban '$detectedAnswer' berhasil disuntikkan ke web!"
                                                questionRepository.writeLog(LogLevel.SUCCESS, "WEB_CBT", "Injeksi DOM web berhasil untuk jawaban '$detectedAnswer'.")
                                            }
                                        }
                                    } catch (e: Exception) {
                                        withContext(Dispatchers.Main) {
                                            isAnalyzing = false
                                            webStatusText = "[GAGAL] ${e.message}"
                                        }
                                    }
                                }
                            }
                        },
                        icon = Icons.Default.Bolt,
                        backgroundColor = NeonCyan,
                        textColor = TextDark,
                        enabled = !isAnalyzing,
                        modifier = Modifier.weight(1f),
                        testTag = "btn_web_autofill"
                    )

                    NeoBadge(
                        text = webStatusText.take(28),
                        color = if (webStatusText.contains("TERISI")) NeonEmerald else NeonYellow,
                        textColor = TextDark
                    )
                }
            }
        }

        if (progress > 0f && progress < 1f) {
            LinearProgressIndicator(
                progress = { progress },
                color = NeonCyan,
                trackColor = CyberBorder,
                modifier = Modifier.fillMaxWidth().height(2.dp)
            )
        }

        // WebView Container
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        cacheMode = WebSettings.LOAD_DEFAULT
                    }
                    webChromeClient = object : WebChromeClient() {
                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            progress = newProgress / 100f
                        }
                    }
                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            url?.let { urlInput = it }
                        }
                        override fun onPageFinished(view: WebView?, url: String?) {
                            progress = 0f
                        }
                    }
                    loadUrl(currentUrl)
                    webViewInstance = this
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        )
    }
}
