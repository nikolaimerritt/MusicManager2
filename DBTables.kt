package sample

data class Song (val songID: Int, val title: String, val artist: String?, val album: String?, val length: Int, val filepath: String)
data class PlaylistSongs (val playlistID: Int, val songID: Int)
data class Playlist (val playlistID: Int, val name: String, val username: String, val isUserEditable: Boolean)
data class User (val username: String, val password: String)