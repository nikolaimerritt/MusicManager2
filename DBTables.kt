package sample

data class Song (val songID: Int, val title: String, val artist: String?, val album: String?, val filepath: String)
data class Playlist (val playlistName: String, val username: String, val isUserEditable: Boolean)
data class User (val username: String, val password: String)