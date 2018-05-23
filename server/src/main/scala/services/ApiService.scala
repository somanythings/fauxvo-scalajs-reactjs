package services

import java.util.{Date, UUID}

import spatutorial.shared._

class ApiService extends Api with OvoApi {
  var todos = Seq(
    TodoItem("41424344-4546-4748-494a-4b4c4d4e4f50", 0x61626364, "Come up with a trite example to demonstrate scalajs fullstack", TodoLow, completed = true),
    TodoItem("2", 0x61626364, "Show fauxvo to ovo", TodoNormal, completed = false),
    TodoItem("3", 0x61626364, "Questions?", TodoNormal, completed = false),
    TodoItem("4", 0x61626364, "Drink", TodoLow, completed = false)
  )

  var meterRead: Int = 0
  var meterReads: Seq[Int] = Seq(23)

  override def welcomeMsg(name: String): String =
    s"Welcome to Fauxvo, $name! Time is now ${new Date}. Please submit your reading!"

  override def getAllTodos(): Seq[TodoItem] = {
    // provide some fake Todos
    Thread.sleep(300)
    println(s"Sending ${todos.size} Todo items")
    todos
  }

  // update a Todo
  override def updateTodo(item: TodoItem): Seq[TodoItem] = {
    // TODO, update database etc :)
    if(todos.exists(_.id == item.id)) {
      todos = todos.collect {
        case i if i.id == item.id => item
        case i => i
      }
      println(s"Todo item was updated: $item")
    } else {
      // add a new item
      val newItem = item.copy(id = UUID.randomUUID().toString)
      todos :+= newItem
      println(s"Todo item was added: $newItem")
    }
    Thread.sleep(300)
    todos
  }

  // delete a Todo
  override def deleteTodo(itemId: String): Seq[TodoItem] = {
    println(s"Deleting item with id = $itemId")
    Thread.sleep(300)
    todos = todos.filterNot(_.id == itemId)
    todos
  }

  override def getLastMeterRead(): Int = meterRead

  override def saveMeterRead(v: Int): String = {
    (meterReads = (meterReads :+ v))
    "Done"
  }

  override def getMeterHistory(): Seq[Int] = meterReads
}
