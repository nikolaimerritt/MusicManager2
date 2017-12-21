package sample

import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import java.io.File

class MP3Player
{
	private val queue = ArrayList<MediaPlayer>()
	var playing = false; private set
	@Volatile var newSong = false
	private var songIndex = 0

	companion object
	{
		fun songsInPlaylist(playlist: Playlist): ArrayList<Song>
		{
			val dbConn = DBConn()
			val query = "SELECT Song.* FROM Song JOIN PlaylistSong ON Song.songID = PlaylistSong.songID WHERE PlaylistSong.playlistName = '${playlist.playlistName}';"
			val resultLines = dbConn.linesFromQuery(query)
			val songsInPlaylist = ArrayList<Song>()

			resultLines.forEach {
				val columns = it.split("\t")
				songsInPlaylist.add(Song(
						columns[0].trim().toInt(),
						columns[1].trim(),
						columns[2].trim(),
						columns[3].trim(),
						columns[4].trim()
			))}

			dbConn.close()
			return songsInPlaylist
		}

		fun allPlaylists(): ArrayList<Playlist>
		{
			val dbConn = DBConn()
			val query = "SELECT Playlist.* FROM Playlist;"
			//val resultSet = dbConn.resultSetFromQuery(query)
			val allPlaylists = ArrayList<Playlist>()

			val resultLines = dbConn.linesFromQuery(query)
			val playlists = ArrayList<Playlist>()

			resultLines.forEach {
				val columns = it.split("\t")
				playlists.add(Playlist(
						columns[0].trim(),
						columns[1].trim(),
						columns[2].trim().toBoolean()
			))}

			dbConn.close()
			return playlists
		}

		fun allPlaylistNames(): ArrayList<String>
		{
			val allNames = ArrayList<String>()
			allPlaylists().forEach { allNames.add(it.playlistName) }
			return allNames
		}

		fun playlistFromName(name: String): Playlist = allPlaylists().filter { it.playlistName == name }[0]

		fun songTitlesInPlaylist(playlist: Playlist): ArrayList<String>
		{
			val allSongNames = ArrayList<String>()
			songsInPlaylist(playlist).forEach { allSongNames.add(it.title) }
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

	fun clear()
	{
		queue.clear()
		songIndex = 0
	}

	fun reload(songs: ArrayList<Song>)
	{
		stop()
		clear()
		push(songs)
		play()
	}

	fun startFromBeginning()
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
		if (newIndex >= queue.size) { println("newIndex is $newIndex, which is out of range of the queue, which has ${queue.size} elements.") }
		else if (newIndex < 0) { println("newIndex is $newIndex, which is less than 0. ") }
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
		newSong = true
		play()
	}

	fun skipBackward()
	{
		stop()
		songIndex = Math.max(songIndex - 1, 0)
		play()
	}

	fun currentSongLength(): Double
	{
		if (queue.isNotEmpty() && songIndex < queue.size && songIndex >= 0) { return queue[songIndex].totalDuration.toMillis() }
		throw Exception("songIndex is $songIndex, whereas queue has ${queue.size} elements.")
	}
}