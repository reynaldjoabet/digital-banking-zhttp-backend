package model

sealed trait AppError extends Exception


object AppError{
  case object CustomerNotFoundException extends AppError
  case object BankAccountNotFoundException extends AppError
  case object BalanceNotSufficientException extends AppError
  case object FailedTransferException extends AppError
}
