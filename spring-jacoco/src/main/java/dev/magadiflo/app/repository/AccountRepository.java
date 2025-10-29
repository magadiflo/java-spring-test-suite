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
     * Actualiza el nombre del titular de una cuenta mediante una consulta SQL nativa.
     * <p>
     * Utiliza {@code @NativeQuery} (Spring Data JPA 3.4+) como atajo de {@code @Query(nativeQuery = true)},
     * junto con SpEL para acceder a las propiedades del objeto {@code account}.
     * </p>
     * <p>
     * Se aplica {@code clearAutomatically = true} para limpiar el contexto de persistencia
     * tras la ejecución del {@code UPDATE}, evitando inconsistencias si se accede a la entidad
     * modificada en el mismo contexto transaccional.
     * </p>
     *
     * @param account la entidad con los datos actualizados (debe contener id y holder)
     * @return número de filas afectadas (1 si la actualización fue exitosa, 0 si no se encontró la cuenta)
     * @implNote Este método debe ejecutarse dentro de un contexto {@code @Transactional}
     */
    @Modifying(clearAutomatically = true)
    @NativeQuery(value = """
            UPDATE accounts
            SET holder = :#{#account.holder}
            WHERE id = :#{#account.id}
            """)
    int updateAccountHolder(Account account);

    /**
     * Elimina una cuenta por su identificador mediante una consulta SQL nativa.
     * <p>
     * Aunque {@link JpaRepository} ya provee {@code deleteById()}, este método permite
     * personalizar la eliminación con SQL nativo.
     * </p>
     * <p>
     * Se aplica {@code clearAutomatically = true} para limpiar el {@code EntityManager}
     * tras el {@code DELETE}, asegurando que la entidad eliminada no permanezca en caché
     * durante el mismo contexto transaccional (especialmente útil en pruebas).
     * </p>
     *
     * @param accountId el identificador de la cuenta a eliminar
     * @return número de filas afectadas (1 si la eliminación fue exitosa, 0 si no se encontró la cuenta)
     * @implNote Este método debe ejecutarse dentro de un contexto {@code @Transactional}
     */
    @Modifying(clearAutomatically = true)
    @Query(value = """
            DELETE FROM accounts
            WHERE id = :accountId
            """, nativeQuery = true)
    int deleteAccountById(Long accountId);
}
