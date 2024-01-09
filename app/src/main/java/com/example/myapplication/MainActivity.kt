package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.FileProvider
import com.example.myapplication.ui.theme.MyApplicationTheme
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                    writeToExcel("Hello Android!", "Sheet1")
                    shareExcelFile("example.xlsx")
                }
            }
        }
    }

    private fun writeToExcel(data: String, sheetName: String) {
        val excelFile = File(cacheDir, "example.xlsx")
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet(sheetName)

        val row = sheet.createRow(0)
        val cell = row.createCell(0)
        cell.setCellValue(data)

        val outputStream = FileOutputStream(excelFile)
        workbook.write(outputStream)
        workbook.close()
        outputStream.close()
    }

    private fun shareExcelFile(fileName: String) {
        val file = File(cacheDir, fileName)
        val uri = FileProvider.getUriForFile(this, "${packageName}.provider", file)

        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}

// Ergänzen Sie diese Klassen und Funktionen in Ihrer MainActivity

@Entity
data class User(
    @PrimaryKey val username: String,
    val password: String
)

@Dao
interface UserDao {
    @Insert
    fun insert(user: User)

    @Query("SELECT * FROM User WHERE username = :username")
    fun getUserByUsername(username: String): User?
}

// Fügen Sie diese Funktionen in Ihre MainActivity ein
private fun insertUser(username: String, password: String) {
    val userDao = AppDatabase.getInstance(this).userDao()
    val user = User(username, password)
    userDao.insert(user)
}

private fun authenticateUser(username: String, password: String): Boolean {
    val userDao = AppDatabase.getInstance(this).userDao()
    val user = userDao.getUserByUsername(username)
    return user != null && user.password == password
}

