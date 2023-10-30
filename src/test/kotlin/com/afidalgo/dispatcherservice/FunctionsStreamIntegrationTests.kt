package com.afidalgo.dispatcherservice

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.IOException
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.stream.binder.test.InputDestination
import org.springframework.cloud.stream.binder.test.OutputDestination
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration
import org.springframework.context.annotation.Import
import org.springframework.integration.support.MessageBuilder
import shared.library.order.OrderAcceptedMessage
import shared.library.order.OrderDispatchedMessage

@SpringBootTest
@Import(TestChannelBinderConfiguration::class)
class FunctionsStreamIntegrationTests {

  @Autowired lateinit var input: InputDestination

  @Autowired lateinit var output: OutputDestination

  @Autowired lateinit var objectMapper: ObjectMapper

  @Test
  @Throws(IOException::class)
  fun whenOrderAcceptedThenDispatched() {
    val orderId: Long = 121
    val inputMessage = MessageBuilder.withPayload(OrderAcceptedMessage(orderId)).build()
    val expectedMessage = MessageBuilder.withPayload(OrderDispatchedMessage(orderId)).build()
    this.input.send(inputMessage)
    val actual =
        objectMapper.readValue(output.receive().payload, OrderDispatchedMessage::class.java)
    assert(actual == expectedMessage.payload)
  }
}
