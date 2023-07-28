package com.example.ttt

import androidx.test.annotation.UiThreadTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.UiThreadTestRule

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 *
 * To run this testing make sure to change 'private fun...' -> 'internal fun...' in MainActivity.kt
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)
    @get:Rule
    val uiThreadRule = UiThreadTestRule()
    @Test
    fun testStartNewGame() {
        val activity = activityRule.activity
        // Ensure the game board is initialized correctly
        assertEquals(3, activity.gameBoard.size)
        assertEquals(3, activity.gameBoard[0].size)
        assertEquals(3, activity.gameBoard[1].size)
        assertEquals(3, activity.gameBoard[2].size)
        // Call startNewGame with setClickListener = false
        activity.startNewGame(false)
        // Ensure the game board is cleared
        for (i in 0 until activity.gameBoard.size) {
            for (j in 0 until activity.gameBoard[i].size) {
                assertEquals(' ', activity.gameBoard[i][j])
            }
        }
        // Ensure the cells in the table layout are empty
        for (i in 0 until activity.tableLayout!!.childCount) {
            val row = activity.tableLayout!!.getChildAt(i) as android.widget.TableRow
            for (j in 0 until row.childCount) {
                val cell = row.getChildAt(j) as android.widget.TextView
                assertEquals("", cell.text)
            }
        }
        // Ensure the turnTextView is updated correctly
        assertEquals("X's Turn", activity.turnTextView?.text)
    }

    @Test
    fun testCellClickListener() {
        activityRule.runOnUiThread {
            val activity = activityRule.activity
            activity.startNewGame(false)
            activity.cellClickListener(0, 0)
            // Add your assertions here
        }
    }
    @Test
    fun testIsBoardFull_WhenBoardIsEmpty_ReturnsFalse() {
        val gameBoard: Array<CharArray> = Array(3) { CharArray(3) { ' ' } }

        val isFull = isBoardFull(gameBoard)

        assertFalse(isFull)
    }

    @Test
    fun testIsBoardFull_WhenBoardIsNotEmpty_ReturnsTrue() {
        val gameBoard: Array<CharArray> = arrayOf(
            charArrayOf('X', 'O', 'X'),
            charArrayOf('O', 'X', 'O'),
            charArrayOf('O', 'X', 'O')
        )
        val isFull = isBoardFull(gameBoard)
        assertTrue(isFull)
    }
    private fun isBoardFull(gameBoard: Array<CharArray>): Boolean {
        for (i in 0 until gameBoard.size) {
            for (j in 0 until gameBoard[i].size) {
                if (gameBoard[i][j] == ' ') {
                    return false
                }
            }
        }
        return true
    }
}