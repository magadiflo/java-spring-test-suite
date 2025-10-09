package dev.magadiflo.app.mapper;

import dev.magadiflo.app.dto.AccountCreateRequest;
import dev.magadiflo.app.dto.AccountResponse;
import dev.magadiflo.app.dto.AccountUpdateRequest;
import dev.magadiflo.app.entity.Account;
import dev.magadiflo.app.entity.Bank;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountMapper {
    @Mapping(target = "bankName", source = "bank.name")
    AccountResponse toAccountResponse(Account account);

    @Mapping(target = "bank", source = "bank")
    Account toAccount(AccountCreateRequest request, Bank bank);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "bank", ignore = true)
    Account toUpdateAccount(AccountUpdateRequest request, @MappingTarget Account account);
}
