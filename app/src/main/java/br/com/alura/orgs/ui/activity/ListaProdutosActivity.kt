package br.com.alura.orgs.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import br.com.alura.orgs.R
import br.com.alura.orgs.database.AppDatabase
import br.com.alura.orgs.databinding.ActivityListaProdutosActivityBinding
import br.com.alura.orgs.ui.recyclerview.adapter.ListaProdutosAdapter
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG = "ListaProdutos"
class ListaProdutosActivity : AppCompatActivity() {
    private val adapter = ListaProdutosAdapter(this)
    private val binding by lazy { ActivityListaProdutosActivityBinding.inflate(layoutInflater) }
    private val produtoDao by lazy { AppDatabase.instancia(this).produtoDao() }
    private val handler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Log.i(TAG, "onResume: throwable $throwable")
        Toast.makeText(
            this@ListaProdutosActivity,
            "Ocorreu um problema",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        configuraRecyclerView()
        configuraFab()
        lifecycleScope.launch(handler) {
            produtoDao.buscaTodos().collect { produtos ->
                adapter.atualiza(produtos)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_ordernar_lista_produtos, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        lifecycleScope.launch {
            when (item.itemId) {
                R.id.menu_lista_produtos_nome_desc      -> adapter.atualiza(produtoDao.listaOrdernadaDesc("nome"))
                R.id.menu_lista_produtos_nome_asc       -> adapter.atualiza(produtoDao.listaOrdernadaAsc("nome"))
                R.id.menu_lista_produtos_descricao_desc -> adapter.atualiza(produtoDao.listaOrdernadaDesc("descricao"))
                R.id.menu_lista_produtos_descricao_asc  -> adapter.atualiza(produtoDao.listaOrdernadaAsc("descricao"))
                R.id.menu_lista_produtos_valor_desc     -> adapter.atualiza(produtoDao.listaOrdernadaDesc("valor"))
                R.id.menu_lista_produtos_valor_asc      -> adapter.atualiza(produtoDao.listaOrdernadaAsc("valor"))
            }
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