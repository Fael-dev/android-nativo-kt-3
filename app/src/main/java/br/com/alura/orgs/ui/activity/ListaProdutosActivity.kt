package br.com.alura.orgs.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import br.com.alura.orgs.R
import br.com.alura.orgs.database.AppDatabase
import br.com.alura.orgs.databinding.ActivityListaProdutosActivityBinding
import br.com.alura.orgs.ui.recyclerview.adapter.ListaProdutosAdapter
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private val TAG= "ListaProdutos"
class ListaProdutosActivity : AppCompatActivity() {
    private val adapter = ListaProdutosAdapter(this)
    private val binding by lazy {
        ActivityListaProdutosActivityBinding.inflate(layoutInflater)
    }
    private val produtoDao by lazy {
        AppDatabase.instancia(this).produtoDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runBlocking {
            Log.i(TAG, "onCreate: runBlocking init")
            launch {
                Log.i(TAG, "onCreate: launch init")
                Thread.sleep(2000)
                Log.i(TAG, "onCreate: launch finish")
            }
            Log.i(TAG, "onCreate: runBlocking finish")
        }
        setContentView(binding.root)
        configuraRecyclerView()
        configuraFab()
    }

    override fun onResume() {
        super.onResume()
        adapter.atualiza(produtoDao.buscaTodos())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_ordernar_lista_produtos, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_lista_produtos_nome_desc      -> { adapter.atualiza(produtoDao.listaOrdernadaDesc("nome")) }
            R.id.menu_lista_produtos_nome_asc       -> { adapter.atualiza(produtoDao.listaOrdernadaAsc("nome")) }
            R.id.menu_lista_produtos_descricao_desc -> { adapter.atualiza(produtoDao.listaOrdernadaDesc("descricao")) }
            R.id.menu_lista_produtos_descricao_asc  -> { adapter.atualiza(produtoDao.listaOrdernadaAsc("descricao")) }
            R.id.menu_lista_produtos_valor_desc     -> { adapter.atualiza(produtoDao.listaOrdernadaDesc("valor")) }
            R.id.menu_lista_produtos_valor_asc      -> { adapter.atualiza(produtoDao.listaOrdernadaAsc("valor")) }
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