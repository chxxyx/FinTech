package com.chxxyx.projectfintech.account.controller;

import com.chxxyx.projectfintech.account.dto.DepositBalance;
import com.chxxyx.projectfintech.account.dto.TransactionList;
import com.chxxyx.projectfintech.account.dto.TransferBalance;
import com.chxxyx.projectfintech.account.dto.WithdrawBalance;
import com.chxxyx.projectfintech.account.exception.AccountException;
import com.chxxyx.projectfintech.account.service.TransactionService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TransactionController {

	private final TransactionService transactionService;

	@PostMapping("/transaction/deposit")
	@PreAuthorize("hasRole('USER')")
	public DepositBalance.Response depositBalance(
		@RequestBody @Valid DepositBalance.Request request) {
		try {
			return DepositBalance.Response.from(
				transactionService.depositBalance(request.getUsername(), request.getPassword(),
					request.getAccountNumber(), request.getAccountPassword(), request.getAmount()));
		} catch (AccountException e) {
			transactionService.saveFailedDepositTransaction(request.getAccountNumber(),
				request.getAmount());
			throw e;
		}
	}

	@PostMapping("/transaction/withdraw")
	@PreAuthorize("hasRole('USER')")
	public WithdrawBalance.Response withdrawBalance(
		@RequestBody @Valid WithdrawBalance.Request request) {
		try {
			log.info("비밀번호   " + request.getAccountPassword());
			return WithdrawBalance.Response.from(
				transactionService.withdrawBalance(request.getUsername(), request.getPassword(),
					request.getAccountNumber(), request.getAccountPassword(), request.getAmount()));
		} catch (AccountException e) {
			transactionService.saveFailedWithdrawTransaction(request.getAccountNumber(),
				request.getAmount());
			throw e;
		}
	}

	@PostMapping("/transaction/transfer")
	@PreAuthorize("hasRole('USER')")
	public TransferBalance.Response transferBalance(
		@RequestBody @Valid TransferBalance.Request request) {
		try {
			return TransferBalance.Response.from(
				transactionService.transferBalance(request.getUsername(), request.getPassword(),
					request.getSenderName(), request.getSenderAccountNumber(),
					request.getAccountPassword(), request.getReceiverName(),
					request.getReceiverAccountNumber(), request.getAmount()));
		} catch (AccountException e) {
			transactionService.saveFailedTransfer(request.getSenderAccountNumber(),
				request.getAmount());
			throw e;
		}
	}

	@GetMapping("/transaction/transactionList.do")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> getTransactionList(
		@RequestBody @Valid TransactionList.Request request) {
		return new ResponseEntity<>(
			transactionService.getTransactionList(request.getUsername(), request.getPassword(),
				request.getAccountNumber(), request.getAccountPassword(), request.getStartDate(),
				request.getEndDate()), HttpStatus.OK);

	}

}
