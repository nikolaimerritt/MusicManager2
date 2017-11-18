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
		fun getAllSongs(): ArrayList<Song>
		{
			val dbConn = DBConn()
			val resultSet = dbConn.resultSetFromQuery("SELECT * FROM Song")
			val allSongs = ArrayList<Song>()

			while (resultSet.next())
			{
				allSongs.add(Song(
					resultSet.getInt("songID"),
					resultSet.getString("title"),
					resultSet.getNString("artist"),
					resultSet.getNString("album"),
					resultSet.getInt("length"),
					resultSet.getString("filepath")
				))
			}

			dbConn.close()
			return allSongs
		}

		fun getAllSongNames(): ArrayList<String>
		{
			val allSongNames = ArrayList<String>()
			getAllSongs().forEach { allSongNames.add(it.title) }
			return allSongNames
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

	private fun mediaPlayerFromSong(song: Song): MediaPlayer
	{
		val uri = File(song.filepath).toURI()
		val media = Media(uri.toString())
		val player = MediaPlayer(media)
		player.onEndOfMedia = Runnable { skipForward() }
		return player
	}

	fun push(song: Song) = queue.add(mediaPlayerFromSong(song))
	fun push(vararg songs: Song) = songs.forEach { push(it) }
	fun push(songs: ArrayList<Song>) = songs.forEach { push(it) }

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