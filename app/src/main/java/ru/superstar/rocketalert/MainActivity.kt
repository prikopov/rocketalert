package ru.superstar.rocketalert

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView

class MainActivity : AppCompatActivity() {

    private lateinit var tvStatusText: TextView
    private lateinit var cardStatus: MaterialCardView
    private lateinit var btnTestSound: Button
    private lateinit var btnRequestPermissions: Button

    private val requiredPermissions = arrayOf(
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.READ_SMS
    )

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val receiveSmsGranted = permissions[Manifest.permission.RECEIVE_SMS] ?: false
        val readSmsGranted = permissions[Manifest.permission.READ_SMS] ?: false

        if (receiveSmsGranted && readSmsGranted) {
            updateUiStatus(true)
            Toast.makeText(this, "Разрешения предоставлены", Toast.LENGTH_SHORT).show()
        } else {
            updateUiStatus(false)
            Toast.makeText(this, "Требуются разрешения для SMS", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvStatusText = findViewById(R.id.tvStatusText)
        cardStatus = findViewById(R.id.cardStatus)
        btnTestSound = findViewById(R.id.btnTestSound)
        btnRequestPermissions = findViewById(R.id.btnRequestPermissions)

        findViewById<TextView>(R.id.tvSenderValue).text = SmsReceiver.SENDER
        findViewById<TextView>(R.id.tvKeywordsValue).text = SmsReceiver.KEYWORDS.joinToString("\n") { "• $it" }

        btnTestSound.setOnClickListener {
            RocketSoundPlayer.playSound(this)
        }

        btnRequestPermissions.setOnClickListener {
            checkAndRequestPermissions()
        }

        findViewById<Button>(R.id.btnTelegram).setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/superstarpublic"))
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Не удалось открыть Telegram", Toast.LENGTH_SHORT).show()
            }
        }

        checkCurrentPermissions()
    }

    override fun onResume() {
        super.onResume()
        checkCurrentPermissions()
    }

    private fun checkCurrentPermissions() {
        val hasAllPermissions = requiredPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
        updateUiStatus(hasAllPermissions)
    }

    private fun checkAndRequestPermissions() {
        val missingPermissions = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            requestPermissionLauncher.launch(requiredPermissions)
        } else {
            updateUiStatus(true)
        }
    }

    private fun updateUiStatus(isActive: Boolean) {
        if (isActive) {
            tvStatusText.text = getString(R.string.status_active)
            tvStatusText.setTextColor(ContextCompat.getColor(this, R.color.status_active))
            cardStatus.strokeColor = ContextCompat.getColor(this, R.color.status_active)
            btnRequestPermissions.visibility = Button.GONE
        } else {
            tvStatusText.text = getString(R.string.status_inactive)
            tvStatusText.setTextColor(ContextCompat.getColor(this, R.color.status_inactive))
            cardStatus.strokeColor = ContextCompat.getColor(this, R.color.status_inactive)
            btnRequestPermissions.visibility = Button.VISIBLE
        }
    }
}
