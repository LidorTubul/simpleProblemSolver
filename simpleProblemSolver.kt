import javax.swing.*
import java.awt.*

interface Problem {
    fun isSolved(): Boolean
    fun getNextState(): List<Problem>
    fun printState()
}

class ProblemSolver {
    fun solve(problem: Problem): Problem? {
        // create a state list, initialize it with the problem (sudoku or graph for now)
        val open = mutableListOf(problem)
        while (open.isNotEmpty()) {
            // check if the next state is a valid solution
            val current = open.removeAt(0)
            if (current.isSolved()) {
                return current
            }
            // add the next states that are closer to a solution to the state list.
            open.addAll(current.getNextState())
        }
        return null
    }
}

class Graph(private val adjacencyMatrix: Array<IntArray>, private val startNode: Int, private val endNode: Int) : Problem {

    private var visitedNodes: Set<Int> = setOf(startNode)

    override fun isSolved(): Boolean {
        return visitedNodes.contains(endNode)
    }

    override fun getNextState(): List<Graph> {
        val nextStates = mutableListOf<Graph>()
        val currentNode = visitedNodes.last()

        for (neighbor in adjacencyMatrix[currentNode].indices) {
            if (adjacencyMatrix[currentNode][neighbor] == 1 && !visitedNodes.contains(neighbor)) {
                val newVisitedNodes = visitedNodes + neighbor
                nextStates.add(Graph(adjacencyMatrix, startNode, endNode, newVisitedNodes))
            }
        }
        return nextStates
    }

    override fun printState() {
        println("Visited: $visitedNodes")
    }

    constructor(adjacencyMatrix: Array<IntArray>, startNode: Int, endNode: Int, visitedNodes: Set<Int>) : this(adjacencyMatrix, startNode, endNode) {
        this.visitedNodes = visitedNodes
    }

    fun getVisitedNodes(): Set<Int> {
        return visitedNodes
    }
}

class GraphGUI(graph: Graph) : JFrame() {
    init {
        title = "Graph Traversal"
        layout = BorderLayout()
        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(400, 400)

        val visited = graph.getVisitedNodes()
        val content = JTextArea("Route: $visited")
        content.isEditable = false
        add(content, BorderLayout.CENTER)
        isVisible = true
    }
}

class Sudoku(private val board: Array<IntArray>) : Problem {

    // checks if all cells filled and no duplicates
    override fun isSolved(): Boolean {
        return board.all { row -> row.all { it != 0 } } && isValidSudoku()
    }

    // generates possible next states
    override fun getNextState(): List<Sudoku> {
        val nextBoards = mutableListOf<Sudoku>()
        for (i in board.indices) {
            for (j in board[i].indices) {
                if (board[i][j] == 0) {
                    for (num in 1..9) {
                        // copy the board
                        val newBoard = board.map { it.clone() }.toTypedArray()
                        newBoard[i][j] = num
                        // only add if the new state is valid
                        if (Sudoku(newBoard).isValidSudoku()) {
                            nextBoards.add(Sudoku(newBoard))
                        }
                    }
                    // return after processing the first empty cell
                    return nextBoards
                }
            }
        }
        return nextBoards
    }

    // checks for duplicates in a row, column or 3x3 square
    private fun isValidUnit(unit: IntArray): Boolean {
        val seen = mutableSetOf<Int>()
        for (num in unit) {
            if (num == 0) continue // Empty cell is not counted
            if (num in seen) return false
            seen.add(num)
        }
        return true
    }

    // validate that the board follows sudoku rules
    private fun isValidSudoku(): Boolean {
        // Check rows
        for (i in 0..9) {
            if (!isValidUnit(board[i])) return false
        }

        // check columns
        for (i in 0..9) {
            val column = IntArray(9) { board[it][i] }
            if (!isValidUnit(column)) return false
        }

        // check 3x3 squares
        for (i in 0..9 step 3) {
            for (j in 0..9 step 3) {
                val block = IntArray(9)
                var index = 0
                for (row in i..i + 3) {
                    for (col in j..j + 3) {
                        block[index++] = board[row][col]
                    }
                }
                if (!isValidUnit(block)) return false
            }
        }

        return true
    }

    // prints the state of the board
    override fun printState() {
        board.forEach { row ->
            println(row.joinToString(" "))
        }
    }

    fun getBoard(): Array<IntArray> {
        return board
    }
}

class SudokuGUI(sudoku: Sudoku) : JFrame() {
    init {
        title = "Sudoku Solver"
        layout = GridLayout(9, 9)
        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(400, 400)
        val board = sudoku.getBoard()

        for (i in 0..8) {
            for (j in 0..8) {
                val cell = JTextField()
                cell.isEditable = false
                cell.horizontalAlignment = JTextField.CENTER
                cell.text = if (board[i][j] == 0) "" else board[i][j].toString()
                add(cell)
            }
        }
        isVisible = true
    }
}

fun main() {
    // testing:
//    val initialBoard = arrayOf(
//        intArrayOf(5, 3, 0, 0, 7, 0, 0, 0, 0),
//        intArrayOf(6, 0, 0, 1, 9, 5, 0, 0, 0),
//        intArrayOf(0, 9, 8, 0, 0, 0, 0, 6, 0),
//        intArrayOf(8, 0, 0, 0, 6, 0, 0, 0, 3),
//        intArrayOf(4, 0, 0, 8, 0, 3, 0, 0, 1),
//        intArrayOf(7, 0, 0, 0, 2, 0, 0, 0, 6),
//        intArrayOf(0, 6, 0, 0, 0, 0, 2, 8, 0),
//        intArrayOf(0, 0, 0, 4, 1, 9, 0, 0, 5),
//        intArrayOf(0, 0, 0, 0, 8, 0, 0, 7, 9)
//    )
//
//    val sudoku = Sudoku(initialBoard)
//    val solver = ProblemSolver()
//
//    val solvedSudoku = solver.solve(sudoku)
//    if (solvedSudoku != null) {
//        SudokuGUI(solvedSudoku as Sudoku)
//    } else {
//        println("No solution found.")
//    }

    // testing
//    val adjacencyMatrix = arrayOf(
//        intArrayOf(0, 1, 0, 0, 1),
//        intArrayOf(1, 0, 1, 0, 0),
//        intArrayOf(0, 1, 0, 1, 1),
//        intArrayOf(0, 0, 1, 0, 1),
//        intArrayOf(1, 0, 1, 1, 0)
//    )
//
//    val graph = Graph(adjacencyMatrix, 0, 3)
//    val solver = ProblemSolver()
//
//    val solvedGraph = solver.solve(graph)
//    if (solvedGraph != null) {
//        GraphGUI(solvedGraph as Graph)
//    } else {
//        println("No path found.")
//    }
}
