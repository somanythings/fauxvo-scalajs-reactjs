package spatutorial.shared

trait Api extends OvoApi {
  // message of the day
  def welcomeMsg(name: String): String

  // get Todo items
  def getAllTodos(): Seq[TodoItem]

  // update a Todo
  def updateTodo(item: TodoItem): Seq[TodoItem]

  // delete a Todo
  def deleteTodo(itemId: String): Seq[TodoItem]
}


trait OvoApi {
  def saveMeterRead(v: Int): String
  def getMeterHistory(): Seq[Int]
}