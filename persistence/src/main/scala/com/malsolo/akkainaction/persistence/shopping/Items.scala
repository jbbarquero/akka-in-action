package com.malsolo.akkainaction.persistence.shopping

object Items {
  def apply(args: Item*): Items = new Items(args.toList)
  def aggregate(List:List[Item]): Items = ???
}

case class Items(list: List[Item]) {
  import Items._

  def add(newItem: Item) = Items.aggregate(list :+ newItem)
  def add(items: Items) = Items.aggregate(list ++  items.list)

  def removeItem(productId: String) = Items.aggregate(list.filterNot(_.productId == productId))

  def updateItem(productId: String, number: Int) = {
    val newList = list.find(_.productId == productId).map { item =>
      list.filterNot(_.productId == productId) :+ item.update(number)
    }.getOrElse(list)
    Items.aggregate(newList)
  }

  def clear = Items()

  def containsProduct(productId: String) = list.exists(_.productId == productId)

}

case class Item(productId: String, number: Int, unitPrice: BigDecimal) {
  def update(number: Int): Item = copy(number = number)

}


