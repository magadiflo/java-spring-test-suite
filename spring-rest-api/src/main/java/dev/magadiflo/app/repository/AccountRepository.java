package dev.magadiflo.app.repository;

import dev.magadiflo.app.dto.AccountResponse;
import dev.magadiflo.app.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Obtiene todas las cuentas junto con el nombre del banco asociado.
     * <p>
     * Utiliza una proyección DTO ({@link dev.magadiflo.app.dto.AccountResponse})
     * directamente en la consulta JPQL.
     * </p>
     */
    @Query("""
            SELECT new dev.magadiflo.app.dto.AccountResponse(a.id, a.holder, a.balance, b.name)
            FROM Account AS a
                JOIN a.bank AS b
            """)
    List<AccountResponse> getAllAccounts();

    /**
     * Busca una cuenta por el nombre del titular.
     * <p>
     * Ejemplo de consulta JPQL con un parámetro nombrado.
     * </p>
     */
    @Query(value = """
            SELECT a
            FROM Account AS a
            WHERE a.holder = :holder
            """)
    Optional<Account> findAccountByHolder(String holder);

    /**
     * Inserta una nueva cuenta utilizando una consulta SQL nativa.
     * <p>
     * Este método usa parámetros con SpEL (Spring Expression Language) para acceder
     * a las propiedades del objeto {@code account}. Aunque la sintaxis parezca referirse
     * directamente a los campos privados (p. ej. {@code :#{#account.holder}}),
     * en realidad SpEL invoca los getters públicos generados por Lombok
     * (por ejemplo, {@code getHolder()}).
     * </p>
     *
     * @param account la entidad a insertar
     * @return el número de filas afectadas (normalmente 1 si la inserción fue exitosa)
     * @implNote Este método debe ejecutarse dentro de un contexto {@code @Transactional}
     */
    @Modifying
    @Query(value = """
            INSERT INTO accounts(holder, balance, bank_id)
            VALUES(:#{#account.holder}, :#{#account.balance}, :#{#account.bank.id})
            """, nativeQuery = true)
    int saveAccount(Account account);

    /**
     * Actualiza el nombre del titular de una cuenta.
     * <p>
     * Ejemplo de consulta nativa usando la anotación {@code @NativeQuery},
     * introducida en Spring Data JPA 3.4+ como atajo de {@code @Query(nativeQuery = true)}.
     * Utiliza SpEL para acceder a las propiedades del objeto {@code account}.
     * </p>
     *
     * @param account la entidad con los datos actualizados (debe contener id y holder)
     * @return el número de filas afectadas (1 si la actualización fue exitosa, 0 si no se encontró la cuenta)
     * @implNote Este método debe ejecutarse dentro de un contexto {@code @Transactional}
     */
    @Modifying
    @NativeQuery(value = """
            UPDATE accounts
            SET holder = :#{#account.holder}
            WHERE id = :#{#account.id}
            """)
    int updateAccountHolder(Account account);

    /**
     * Elimina una cuenta por su identificador.
     * <p>
     * Se define manualmente como práctica de consultas {@code @Modifying} con DELETE,
     * aunque {@link JpaRepository} ya provee el método {@code deleteById()}.
     * Utiliza una consulta SQL nativa con parámetro nombrado.
     * </p>
     *
     * @param accountId el identificador de la cuenta a eliminar
     * @return el número de filas afectadas (1 si la eliminación fue exitosa, 0 si no se encontró la cuenta)
     * @implNote Este método debe ejecutarse dentro de un contexto {@code @Transactional}
     */
    @Modifying
    @Query(value = """
            DELETE FROM accounts
            WHERE id = :accountId
            """, nativeQuery = true)
    int deleteAccountById(Long accountId);
}
