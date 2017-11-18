package sample

import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import java.io.File

class MP3Player
{
	private val queue = ArrayList<MediaPlayer>()
	private var playing = false
	private var songIndex = 0

	companion object
	{
		fun getAllSongNames(): ArrayList<String>
		{
			val dbConn = DBConn()
			val resultSet = dbConn.resultSetFromQuery("SELECT title FROM Song")
			val songNames = ArrayList<String>()

			while (resultSet.next()) { songNames.add(resultSet.getString("title")) }

			dbConn.close()
			return songNames
		}
	}

	fun play()
	{
		if (!playing)
		{
			queue[songIndex].play()
			playing = true
		}
	}
	fun pause()
	{
		if (playing)
		{
			queue[songIndex].pause()
			playing = false
		}

	}
	fun togglePlayPause() = if (playing) pause() else play()

	private fun mediaPlayerFromName(name: String): MediaPlayer
	{
		val media = Media(File("Songs\\$name.mp3").toURI().toString())
		val player = MediaPlayer(media)
		player.onEndOfMedia = Runnable { skipForward() }
		return player
	}

	fun push(name: String) = queue.add(mediaPlayerFromName(name))
	fun push(vararg names: String) = names.forEach { push(it) }
	fun push(names: ArrayList<String>) = names.forEach { push(it) }

	fun clear() = queue.clear()

	fun startQueueFromBeginning()
	{
		if (queue.isEmpty()) { println("Can't play an empty queue :(") }
		else
		{
			songIndex = 0
			play()
		}
	}

	fun stop()
	{
		if (playing)
		{
			queue[songIndex].stop()
			playing = false
		}
	}

	fun seekTo(percentage: Double)
	{
		if (percentage > 100.0 || percentage < 0.0) { println("Cannot seek to $percentage, which is not in the interval [0, 100]") }
		else
		{
			queue[songIndex].seek(queue[songIndex].totalDuration.multiply(percentage / 100.0))
			queue[songIndex].play()
		}
	}

	fun skipTo(newIndex: Int)
	{
		if (newIndex >= queue.size) { println("$newIndex is out of range of the queue, which has ${queue.size} elements.") }
		else
		{
			stop()
			songIndex = newIndex
			play()
		}
	}

	fun skipForward()
	{
		stop()
		if (songIndex + 1 < queue.size) { songIndex++ }
		else { songIndex = 0 }
		play()
	}

	fun skipBackward()
	{
		stop()
		if (songIndex - 1 < 0) { songIndex = queue.size - 1 }
		else { songIndex-- }
		play()
	}
}