package model

sealed trait AppError extends Exception


object AppError{

  case object CustomerNotFoundException extends AppError
}
