package com.example.ttt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TableLayout
import android.widget.TextView
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        turnTextView = findViewById(R.id.turnTextView) as TextView
        tableLayout = findViewById(R.id.table_layout) as TableLayout
        resetButton = findViewById(R.id.resetButton) as android.widget.Button
        resetButton!!.setOnClickListener() { startNewGame(false) }
        startNewGame(true)
        startTimer()
        timerTextView = findViewById(R.id.timerTextView)

    }
    var gameBoard: Array<CharArray> = Array(3) { CharArray(3) }
    var turn = 'X'
    var tableLayout: android.widget.TableLayout? = null
    var turnTextView: android.widget.TextView? = null
    var resetButton: android.widget.Button? = null
    private var timer: CountDownTimer? = null
    private var isTimerRunning = false
    private var remainingTimeMillis: Long = 0
    private lateinit var timerTextView: TextView



    private fun startTimer() {
        timer = object : CountDownTimer(10000, 1000) { // Timer for 60 seconds (adjust as needed)
            override fun onTick(millisUntilFinished: Long) {
                remainingTimeMillis = millisUntilFinished
                timerTextView.text = "Time remaining: ${millisUntilFinished / 1000} seconds"

                // Update the timer UI element
                // For example: timerTextView.text = "Time remaining: ${millisUntilFinished / 1000} seconds"
            }
            override fun onFinish() {
                timerTextView.text = "Time's up!"
                // Timer has finished, handle as needed
            }
        }
        timer?.start()
        isTimerRunning = true
    }

    private fun stopTimer() {
        timer?.cancel()
        isTimerRunning = false
    }

    private fun restartTimer() {
        stopTimer()
        startTimer()
    }


    internal fun startNewGame(setClickListener: Boolean) {
        turn = 'X'
        turnTextView?.text =
            String.format(resources.getString(R.string.turn), turn)
        for (i in 0 until gameBoard.size) {
            for (j in 0 until gameBoard[i].size) {
                gameBoard[i][j] = ' '
                val cell = (tableLayout?.getChildAt(i) as
                        android.widget.TableRow).getChildAt(j) as android.widget.TextView
                cell.text = ""
                // Cancel the timer and reset the remaining time
                stopTimer()
                remainingTimeMillis = 0
                // Restart the timer
                if (isTimerRunning) {
                    restartTimer()
                } else {
                    startTimer()
                }
                // Reset text color to the default color
                cell.setTextColor(resources.getColor(android.R.color.black))
                if (setClickListener) {
                    cell.setOnClickListener { cellClickListener(i, j) }
                }
            }
        }
    }
    internal fun cellClickListener(row: Int, column: Int) {
        if (gameBoard[row][column] == ' ') {
            gameBoard[row][column] = turn

            ((tableLayout?.getChildAt(row) as android.widget.TableRow).getChildAt(column) as
                    TextView).text = turn.toString()
            if (checkGameStatus()) {
                return
            }

            turn = if ('X' == turn) 'O' else 'X'
            turnTextView?.text = String.format(resources.getString(R.string.turn), turn)

            if (turn == 'O') {
                makeBotMove()
            }
        }
    }

    private fun makeBotMove() {
        val availableCells = mutableListOf<Pair<Int, Int>>()
        for (i in 0 until gameBoard.size) {
            for (j in 0 until gameBoard[i].size) {
                if (gameBoard[i][j] == ' ') {
                    availableCells.add(Pair(i, j))
                }
            }
        }
        if (availableCells.isNotEmpty()) {
            val randomCell = availableCells[Random.nextInt(availableCells.size)]
            val row = randomCell.first
            val column = randomCell.second

            cellClickListener(row, column)
        }
    }

    internal fun isBoardFull(gameBoard: Array<CharArray>): Boolean {
        for (i in 0 until gameBoard.size) {
            for (j in 0 until gameBoard[i].size) {
                if (gameBoard[i][j] == ' ') {
                    return false
                }
            }
        }
        return true
    }
    private fun isWinner(gameBoard: Array<CharArray>, w: Char): Boolean {
        for (i in 0 until gameBoard.size) {
            if (gameBoard[i][0] == w && gameBoard[i][1] == w && gameBoard[i][2] == w) {
                return true
            }

            if (gameBoard[0][i] == w && gameBoard[1][i] == w && gameBoard[2][i] == w) {
                return true
            }
        }

        if ((gameBoard[0][0] == w && gameBoard[1][1] == w && gameBoard[2][2] == w) ||
            (gameBoard[0][2] == w && gameBoard[1][1] == w && gameBoard[2][0] == w)
        ) {
            return true
        }
        return false
    }
    private fun checkGameStatus(): Boolean {
        var state: String? = null
        var winner: Char? = null
        var isDraw = false
        if (isWinner(gameBoard, 'X')) {
            state = String.format(resources.getString(R.string.winner), 'X')
        } else if (isWinner(gameBoard, 'O')) {
            state = String.format(resources.getString(R.string.winner), 'O')
        } else {
            if (isBoardFull(gameBoard)) {
                state = resources.getString(R.string.draw)
                isDraw = true
            }
        }
        if (state != null) {
            turnTextView?.text = state
            // Change the text color of the cells for the winner or reset to black for a draw
            for (i in 0 until gameBoard.size) {
                for (j in 0 until gameBoard[i].size) {
                    val cell = (tableLayout?.getChildAt(i) as android.widget.TableRow)
                        .getChildAt(j) as android.widget.TextView
                    if (isDraw) {
                        cell.setTextColor(resources.getColor(android.R.color.holo_red_dark))
                    } else if (winner != null && gameBoard[i][j] == winner) {
                        cell.setTextColor(resources.getColor(R.color.red))
                    } else {
                        cell.setTextColor(resources.getColor(R.color.green))
                    }
                }
            }
            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            builder.setMessage(state)
            builder.setPositiveButton(android.R.string.ok) { dialog, id ->
                startNewGame(false)
            }
            val dialog = builder.create()
            dialog.show()
            // Game has ended, stop the timer
            stopTimer()
            remainingTimeMillis = 0
            return true // Game has ended
        }
        return false // Game is still ongoing
    }
}
