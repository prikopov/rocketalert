package ru.superstar.rocketalert

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log

object RocketSoundPlayer {
    private const val TAG = "RocketSoundPlayer"
    private var mediaPlayer: MediaPlayer? = null

    @Synchronized
    fun playSound(context: Context) {
        try {
            stopSound()

            val player = MediaPlayer()
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            player.setAudioAttributes(audioAttributes)

            val afd = context.resources.openRawResourceFd(R.raw.rocket)
            player.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()

            player.prepare()
            player.setOnCompletionListener {
                stopSound()
            }

            mediaPlayer = player
            player.start()
        } catch (e: Exception) {
            Log.e(TAG, "Error playing sound: ${e.message}", e)
        }
    }

    @Synchronized
    fun stopSound() {
        try {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    player.stop()
                }
                player.release()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping sound: ${e.message}", e)
        } finally {
            mediaPlayer = null
        }
    }
}
