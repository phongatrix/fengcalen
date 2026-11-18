package fengcalen.util

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import fengcalen.R

data class NokiaToneItem(
    val id: String,
    val name: String,
    val subtitle: String,
    val resId: Int
)

object NokiaSoundHelper {

    val TONES = listOf(
        NokiaToneItem(
            id = "nokia_tune",
            name = "Nokia Tune",
            subtitle = "Nhạc chuông huyền thoại Nokia 3310 (Gran Vals)",
            resId = R.raw.nokia_tune
        ),
        NokiaToneItem(
            id = "nokia_alarm",
            name = "Nokia Báo Thức",
            subtitle = "Tiếng chuông đồng hồ báo thức Nokia cổ điển (Clock Alert)",
            resId = R.raw.nokia_alarm
        ),
        NokiaToneItem(
            id = "nokia_sms",
            name = "Nokia Tin Nhắn SMS",
            subtitle = "Âm báo tin nhắn Morse Special kinh điển (... -- ...)",
            resId = R.raw.nokia_sms
        ),
        NokiaToneItem(
            id = "nokia_polyphonic",
            name = "Nokia Đa Âm (Polyphonic)",
            subtitle = "Giai điệu Nokia hòa âm đa bè phong cách cổ điển",
            resId = R.raw.nokia_polyphonic
        ),
        NokiaToneItem(
            id = "default",
            name = "Mặc định hệ thống",
            subtitle = "Âm thanh thông báo mặc định của thiết bị Android",
            resId = 0
        )
    )

    private var previewPlayer: MediaPlayer? = null
    var currentlyPlayingId: String? = null
        private set

    fun playPreview(context: Context, toneId: String, onFinished: () -> Unit = {}) {
        stopPreview()
        val item = TONES.find { it.id == toneId } ?: return
        if (item.resId != 0) {
            try {
                previewPlayer = MediaPlayer.create(context, item.resId)?.apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .build()
                    )
                    setOnCompletionListener {
                        stopPreview()
                        onFinished()
                    }
                    start()
                }
                currentlyPlayingId = toneId
            } catch (e: Exception) {
                e.printStackTrace()
                stopPreview()
            }
        }
    }

    fun stopPreview() {
        try {
            previewPlayer?.stop()
            previewPlayer?.release()
        } catch (_: Exception) {}
        previewPlayer = null
        currentlyPlayingId = null
    }

    fun getSoundUri(context: Context, toneId: String): Uri? {
        val item = TONES.find { it.id == toneId }
        return if (item != null && item.resId != 0) {
            Uri.parse("android.resource://${context.packageName}/${item.resId}")
        } else {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }
    }

    fun playAlarmSound(context: Context, toneId: String): MediaPlayer? {
        val item = TONES.find { it.id == toneId }
        return if (item != null && item.resId != 0) {
            try {
                MediaPlayer.create(context, item.resId)?.apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .build()
                    )
                    start()
                }
            } catch (e: Exception) {
                null
            }
        } else null
    }
}
