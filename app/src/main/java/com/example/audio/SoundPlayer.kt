package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

object SoundPlayer {
    var isMuted: Boolean = false
    private val scope = CoroutineScope(Dispatchers.Default)
    private const val SAMPLE_RATE = 22050

    private fun playPcm(generator: () -> ShortArray) {
        if (isMuted) return
        scope.launch {
            try {
                val samples = generator()
                val minBuff = AudioTrack.getMinBufferSize(
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
                val bufferSize = maxOf(minBuff, samples.size * 2)
                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(SAMPLE_RATE)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(samples, 0, samples.size)
                track.play()
                Thread.sleep((samples.size * 1000L / SAMPLE_RATE) + 50)
                track.stop()
                track.release()
            } catch (_: Exception) {
            }
        }
    }

    fun playPop() {
        playPcm {
            val durationMs = 45
            val count = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(count)
            for (i in 0 until count) {
                val progress = i.toDouble() / count
                val freq = 600.0 + (1200.0 - 600.0) * (1.0 - progress)
                val envelope = 1.0 - progress
                val wave = sin(2.0 * Math.PI * freq * i / SAMPLE_RATE)
                buffer[i] = (wave * envelope * 24000).toInt().toShort()
            }
            buffer
        }
    }

    fun playBoing() {
        playPcm {
            val durationMs = 120
            val count = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(count)
            for (i in 0 until count) {
                val progress = i.toDouble() / count
                val freq = 320.0 + 160.0 * sin(2.0 * Math.PI * 18.0 * progress)
                val envelope = (1.0 - progress) * (1.0 - progress)
                val wave = sin(2.0 * Math.PI * freq * i / SAMPLE_RATE)
                buffer[i] = (wave * envelope * 25000).toInt().toShort()
            }
            buffer
        }
    }

    fun playSparkle() {
        playPcm {
            val durationMs = 200
            val count = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(count)
            val notes = doubleArrayOf(1046.5, 1318.5, 1567.98, 2093.0)
            for (i in 0 until count) {
                val t = i.toDouble() / SAMPLE_RATE
                val noteIdx = ((i.toDouble() / count) * notes.size).toInt().coerceIn(0, notes.size - 1)
                val freq = notes[noteIdx]
                val subProg = ((i.toDouble() / count) * notes.size) - noteIdx
                val env = (1.0 - subProg) * (1.0 - (i.toDouble() / count))
                val wave = sin(2.0 * Math.PI * freq * t) + 0.3 * sin(4.0 * Math.PI * freq * t)
                buffer[i] = (wave * env * 18000).toInt().toShort()
            }
            buffer
        }
    }

    fun playBubble() {
        playPcm {
            val durationMs = 80
            val count = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(count)
            for (i in 0 until count) {
                val progress = i.toDouble() / count
                val freq = 400.0 + 800.0 * progress
                val env = sin(Math.PI * progress)
                val wave = sin(2.0 * Math.PI * freq * i / SAMPLE_RATE)
                buffer[i] = (wave * env * 22000).toInt().toShort()
            }
            buffer
        }
    }

    fun playFirework() {
        playPcm {
            val durationMs = 180
            val count = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(count)
            var lastNoise = 0.0
            for (i in 0 until count) {
                val progress = i.toDouble() / count
                val noise = (Math.random() * 2.0 - 1.0)
                lastNoise = lastNoise * 0.4 + noise * 0.6
                val whistle = sin(2.0 * Math.PI * (1600.0 - 800.0 * progress) * i / SAMPLE_RATE) * 0.4
                val env = (1.0 - progress) * (1.0 - progress)
                buffer[i] = ((lastNoise * 0.6 + whistle) * env * 24000).toInt().toShort()
            }
            buffer
        }
    }

    fun playChalk() {
        playPcm {
            val durationMs = 60
            val count = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(count)
            for (i in 0 until count) {
                val progress = i.toDouble() / count
                val noise = (Math.random() * 2.0 - 1.0)
                val env = sin(Math.PI * progress)
                buffer[i] = (noise * env * 16000).toInt().toShort()
            }
            buffer
        }
    }

    fun playFill() {
        playPcm {
            val durationMs = 140
            val count = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(count)
            for (i in 0 until count) {
                val progress = i.toDouble() / count
                val freq = 440.0 + 200.0 * sin(2.0 * Math.PI * 6.0 * progress)
                val env = sin(Math.PI * progress)
                val wave = sin(2.0 * Math.PI * freq * i / SAMPLE_RATE)
                buffer[i] = (wave * env * 22000).toInt().toShort()
            }
            buffer
        }
    }

    fun playWhoosh() {
        playPcm {
            val durationMs = 100
            val count = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(count)
            for (i in 0 until count) {
                val progress = i.toDouble() / count
                val freq = 700.0 - 450.0 * progress
                val env = sin(Math.PI * progress)
                val wave = sin(2.0 * Math.PI * freq * i / SAMPLE_RATE)
                buffer[i] = (wave * env * 22000).toInt().toShort()
            }
            buffer
        }
    }

    fun playSplash() {
        playPcm {
            val durationMs = 150
            val count = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(count)
            var lastNoise = 0.0
            for (i in 0 until count) {
                val progress = i.toDouble() / count
                val whiteNoise = (Math.random() * 2.0 - 1.0)
                lastNoise = lastNoise * 0.7 + whiteNoise * 0.3
                val env = (1.0 - progress) * (1.0 - progress)
                buffer[i] = (lastNoise * env * 22000).toInt().toShort()
            }
            buffer
        }
    }

    fun playCelebration() {
        playPcm {
            val durationMs = 450
            val count = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(count)
            val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.5)
            for (i in 0 until count) {
                val noteIdx = ((i.toDouble() / count) * notes.size).toInt().coerceIn(0, notes.size - 1)
                val freq = notes[noteIdx]
                val subProg = ((i.toDouble() / count) * notes.size) - noteIdx
                val env = (1.0 - subProg) * 0.9 + 0.1
                val wave = sin(2.0 * Math.PI * freq * i / SAMPLE_RATE) + 0.25 * sin(4.0 * Math.PI * freq * i / SAMPLE_RATE)
                buffer[i] = (wave * env * 20000).toInt().toShort()
            }
            buffer
        }
    }

    fun playTrophyUnlock() {
        playPcm {
            val durationMs = 500
            val count = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(count)
            val notes = doubleArrayOf(659.25, 783.99, 987.77, 1318.5)
            for (i in 0 until count) {
                val noteIdx = ((i.toDouble() / count) * notes.size).toInt().coerceIn(0, notes.size - 1)
                val freq = notes[noteIdx]
                val wave = sin(2.0 * Math.PI * freq * i / SAMPLE_RATE)
                val env = 1.0 - (i.toDouble() / count)
                buffer[i] = (wave * env * 24000).toInt().toShort()
            }
            buffer
        }
    }
}
