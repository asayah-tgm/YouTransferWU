package at.sayah.you_transfer.interfaces.solid.exception

class InvalidScopeValueException(value: String) : IllegalArgumentException("Invalid scope value $value")