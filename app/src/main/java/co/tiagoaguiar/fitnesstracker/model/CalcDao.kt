package co.tiagoaguiar.fitnesstracker.model

import androidx.room.*

@Dao
interface CalcDao {

    @Insert
    fun insert(calc: Calc)

    @Query("SELECT * FROM Calc WHERE type = :type")
    fun getRegisterByType(type: String) : List<Calc>

    @Delete
    fun delete(calc: Calc): Int // FIXME: retorna 1 se deu sucesso

    @Update
    fun update(calc: Calc)

}