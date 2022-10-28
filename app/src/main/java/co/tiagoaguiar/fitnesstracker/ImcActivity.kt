package co.tiagoaguiar.fitnesstracker

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import co.tiagoaguiar.fitnesstracker.model.Calc

class ImcActivity : AppCompatActivity() {

    private lateinit var editWeight: EditText
    private lateinit var editHeight: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imc)

        editWeight = findViewById(R.id.edit_imc_weight)
        editHeight = findViewById(R.id.edit_imc_height)

        val btnSend: Button = findViewById(R.id.btn_imc_send)

        btnSend.setOnClickListener {
            if (!validate()) {
                Toast.makeText(this, R.string.fields_messages, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val weight = editWeight.text.toString().toInt()
            val height = editHeight.text.toString().toInt()

            val result = calculateImc(weight, height)
            Log.d("Teste", "resultado: $result")

            val imcResponseId = imcResponse(result)

            AlertDialog.Builder(this)
                .setTitle(getString(R.string.imc_response, result))
                .setMessage(imcResponseId)
                .setPositiveButton(android.R.string.ok) { dialog, which ->
                    // aqui vai rodar depois do click
                }
                .setNegativeButton(R.string.save) { dialog, which ->
                    Thread {
                        val app = application as App
                        val dao = app.db.calcDao()

                        // FIXME: checa se tem um updateId, se tiver, significa
                        // FIXME: que viemos da tela da lista de itens e devemos
                        // FIXME: editar ao inves de inserir
                        val updateId = intent.extras?.getInt("updateId")
                        if (updateId != null) {
                            dao.update(Calc(id = updateId, type = "imc", res = result))
                        } else {
                            dao.insert(Calc(type = "imc", res = result))
                        }

                        runOnUiThread {
                            openListActivity()
                        }
                    }.start()

                }
                .create()
                .show()

            val service = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            service.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_search) {
            finish()
            openListActivity()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openListActivity() {
        val intent = Intent(this, ListCalcActivity::class.java)
        intent.putExtra("type", "imc")
        startActivity(intent)
    }

    @StringRes
    private fun imcResponse(imc: Double): Int {
        return when {
            imc < 15.0 -> R.string.imc_severely_low_weight
            imc < 16.0 -> R.string.imc_very_low_weight
            imc < 18.5 -> R.string.imc_low_weight
            imc < 25.0 -> R.string.normal
            imc < 30.0 -> R.string.imc_high_weight
            imc < 35.0 -> R.string.imc_so_high_weight
            imc < 40.0 -> R.string.imc_severely_high_weight
            else -> R.string.imc_extreme_weight
        }
//        if (imc < 15.0) {
//            return R.string.imc_severely_low_weight
//        } else if (imc < 16.0) {
//            return R.string.imc_very_low_weight
//        } else if (imc < 18.5) {
//            return R.string.imc_low_weight
//        } else if (imc < 25.0) {
//            return R.string.normal
//        } else if (imc < 30.0) {
//            return R.string.imc_high_weight
//        } else if (imc < 35.0) {
//            return R.string.imc_so_high_weight
//        } else if (imc < 40.0) {
//            return R.string.imc_severely_high_weight
//        } else {
//            return R.string.imc_extreme_weight
//        }

    }

    private fun calculateImc(weight: Int, height: Int): Double {
        // peso / (altura * altura)
        return weight / ( (height / 100.0) * (height / 100.0) )
    }

    private fun validate(): Boolean {
        return (editWeight.text.toString().isNotEmpty()
            && editHeight.text.toString().isNotEmpty()
            && !editWeight.text.toString().startsWith("0")
            && !editHeight.text.toString().startsWith("0"))
    }

}