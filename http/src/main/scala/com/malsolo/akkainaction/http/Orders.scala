package com.malsolo.akkainaction.http

case class Order(customerId: String, productId: String, number: Int)
case class TrackingOrder(id: Long, status: String, order: Order)
case class OrderId(id: Long)
case class NoSuchOrder(id: Long)
