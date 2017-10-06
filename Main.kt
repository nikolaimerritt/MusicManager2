package sample

import javafx.application.Application
import javafx.collections.FXCollections
import javafx.embed.swt.FXCanvas
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.GridPane
import javafx.stage.Stage
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.scene.control.ListView
import javafx.scene.control.ProgressBar
import javafx.scene.control.TextField
import javafx.event.ActionEvent
import java.util.*

class Main : Application()
{
    private val trackQueue = PriorityQueue<String>()
    override fun start(stage: Stage)
    {
        // initialising stage
        val grid = GridPane()
        val scene = Scene(grid, 200.0, 200.0)
        stage.scene = scene
        stage.title = "Music Manager"
        stage.setOnCloseRequest { System.exit(0) }
        grid.padding = Insets(10.0, 10.0, 10.0, 10.0)
        grid.vgap = 5.0
        grid.hgap = 5.0

        // adding user login button
        val loginButton = Button("Login")
        loginButton.setOnAction { login() }
        GridPane.setConstraints(loginButton, 0, 0)
        grid.children.add(loginButton)

        // adding search box
        val searchBox = TextField()
        searchBox.promptText = "Search me baby..."
        GridPane.setConstraints(searchBox, 1, 0, 97, 1)
        grid.children.add(searchBox)

        // add plus button
        val plusButton = Button("+")
        plusButton.setOnAction { addSong() }
        GridPane.setConstraints(plusButton, 98, 0)
        grid.children.add(plusButton)

        // add song table
        val listView = ListView<String>(FXCollections.observableArrayList(trackQueue))
        listView.orientation = Orientation.VERTICAL
        GridPane.setConstraints(listView, 0, 1, 100, 1)
        grid.children.add(listView)

        // add play/pause button
        val playPauseButton = Button("▮▶")
        playPauseButton.setOnAction { playPause() }
        GridPane.setConstraints(playPauseButton, 0, 2)
        grid.children.add(playPauseButton)

        // add progress bar
        val progressBar = ProgressBar(0.0)
        progressBar.maxWidth = Double.MAX_VALUE
        progressBar.onMouseClicked {(event) -> seek(event)}

        stage.show()
    }

    private fun login() {}
    private fun addSong() {}
    private fun playPause() {}
    private fun seek(event: javafx.event.ActionEvent) {}

    companion object
    {
        @JvmStatic
        fun main(args: Array<String>) { launch(Main::class.java) }
    }

}