package br.com.alura.orgs.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.alura.orgs.R
import br.com.alura.orgs.database.AppDatabase
import br.com.alura.orgs.databinding.ActivityListaProdutosActivityBinding
import br.com.alura.orgs.ui.recyclerview.adapter.ListaProdutosAdapter
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

private val TAG= "ListaProdutos"
class ListaProdutosActivity : AppCompatActivity() {
    private val adapter = ListaProdutosAdapter(this)
    private val binding by lazy { ActivityListaProdutosActivityBinding.inflate(layoutInflater) }
    private val produtoDao by lazy { AppDatabase.instancia(this).produtoDao() }
    private val scope = MainScope() // Cria um novo Scopo na Thread principal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        configuraRecyclerView()
        configuraFab()
    }

    override fun onResume() {
        super.onResume()
        val handler = CoroutineExceptionHandler { coroutineContext, throwable ->
            Log.i(TAG, "onResume: throwable $throwable")
            Toast.makeText(
                this@ListaProdutosActivity,
                "Ocorreu um problema",
                Toast.LENGTH_SHORT
            ).show()
        }
        val job = Job()
        scope.launch(job + handler + IO + CoroutineName("primaria")) { // Passando job, handlerException, Scope e o nome da coroutine
            Log.i(TAG, "onRsume: coroutine context: $coroutineContext")
            repeat(100) {
                Log.i(TAG, "onResume: coroutine está em execução $it")
                delay(1000)
            }
        }
        job.cancel() // Cancela somente essa execução do Coroutine para evitar o consumo exagerado de memória
        // scope.cancel() // => Iria cancelar todo esse scopo de coroutine
        scope.launch(handler) {
            MainScope().launch(handler) {
                throw Exception("Lançando uma nova Exception de teste dentro de outro escopo")
            }
            throw Exception("Lançando uma Exception de teste") // O try não consegue capturar uma Exception dentro do launch
            val produtos = withContext(IO) { // Cria uma nova Thread paralela a Thread principal
                produtoDao.buscaTodos()
            }
            adapter.atualiza(produtos)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_ordernar_lista_produtos, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        scope.launch {
            val produtos = withContext(IO) {
                when (item.itemId) {
                    R.id.menu_lista_produtos_nome_desc      -> produtoDao.listaOrdernadaDesc("nome")
                    R.id.menu_lista_produtos_nome_asc       -> produtoDao.listaOrdernadaAsc("nome")
                    R.id.menu_lista_produtos_descricao_desc -> produtoDao.listaOrdernadaDesc("descricao")
                    R.id.menu_lista_produtos_descricao_asc  -> produtoDao.listaOrdernadaAsc("descricao")
                    R.id.menu_lista_produtos_valor_desc     -> produtoDao.listaOrdernadaDesc("valor")
                    R.id.menu_lista_produtos_valor_asc      -> produtoDao.listaOrdernadaAsc("valor")
                    else -> produtoDao.buscaTodos()
                }
            }
            adapter.atualiza(produtos)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun configuraFab() {
        val fab = binding.activityListaProdutosFab
        fab.setOnClickListener {
            vaiParaFormularioProduto()
        }
    }

     private fun vaiParaFormularioProduto() {
        val intent = Intent(this, FormularioProdutoActivity::class.java)
        startActivity(intent)
    }

    private fun configuraRecyclerView() {
        val recyclerView = binding.activityListaProdutosRecyclerView
        recyclerView.adapter = adapter
        adapter.quandoClicaNoItem = {
            val intent = Intent(
                this,
                DetalhesProdutoActivity::class.java
            ).apply {
                putExtra(CHAVE_PRODUTO_ID, it.id)
            }
            startActivity(intent)
        }
    }

}