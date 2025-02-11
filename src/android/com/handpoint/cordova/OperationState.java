package com.handpoint.cordova;

import com.handpoint.api.shared.Currency;
import com.handpoint.api.shared.operations.Operations;
import com.handpoint.api.shared.options.Options;

import java.math.BigInteger;

public class OperationState {

  Operations type;
  BigInteger amount;
  Currency currency;
  Options options;
  String originalTransactionId;

  public OperationState(Operations type, BigInteger amount, Currency currency, Options options) {
    this.type = type;
    this.amount = amount;
    this.currency = currency;
    this.options = options;
  }

  public OperationState(Operations type, BigInteger amount, Currency currency, Options options, String originalTransactionId) {
    this.type = type;
    this.amount = amount;
    this.currency = currency;
    this.options = options;
    this.originalTransactionId = originalTransactionId;
  }

  public OperationState(Operations type, BigInteger amount, Currency currency, String originalTransactionId) {
    this.type = type;
    this.amount = amount;
    this.currency = currency;
    this.originalTransactionId = originalTransactionId;
  }

  public OperationState(Operations type, String originalTransactionId) {
    this.type = type;
    this.originalTransactionId = originalTransactionId;
  }

}
