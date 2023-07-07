package br.com.alura.orgs.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.alura.orgs.model.Produto

@Dao
interface ProdutoDao {

    @Query("SELECT * FROM Produto")
    fun buscaTodos(): List<Produto>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun salva(vararg produto: Produto)

    @Delete
    fun remove(produto: Produto)

    @Update
    fun altera(produto: Produto)

    @Query("SELECT * FROM Produto WHERE id = :id")
    fun buscaPorId(id: Long): Produto?
    @Query("SELECT * FROM Produto ORDER BY "+
            "CASE :column "+
            "WHEN 'nome' THEN nome "+
            "WHEN 'descricao' THEN descricao "+
            "WHEN 'valor' THEN valor "+
            "ELSE id "+
            "END ASC")
    fun listaOrdernadaAsc(column: String?): List<Produto>

    @Query("SELECT * FROM Produto ORDER BY "+
            "CASE :column "+
            "WHEN 'nome' THEN nome "+
            "WHEN 'descricao' THEN descricao "+
            "WHEN 'valor' THEN valor "+
            "ELSE id "+
            "END DESC")
    fun listaOrdernadaDesc(column: String?): List<Produto>
}