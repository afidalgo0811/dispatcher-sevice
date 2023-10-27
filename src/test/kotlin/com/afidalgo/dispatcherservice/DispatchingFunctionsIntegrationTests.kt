package com.afidalgo.dispatcherservice

import java.util.function.Function
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.function.context.FunctionCatalog
import org.springframework.cloud.function.context.test.FunctionalSpringBootTest
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import shared.library.order.OrderAcceptedMessage
import shared.library.order.OrderDispatchedMessage

@FunctionalSpringBootTest
class DispatchingFunctionsIntegrationTests(@Autowired val catalog: FunctionCatalog) {

  @Disabled
  @Test
  fun packAndLabelOrder() {
    val packAndLabel: Function<OrderAcceptedMessage, Flux<OrderDispatchedMessage>> =
        catalog.lookup(Function::class.java, "pack|label")
    val orderId = 121L
    StepVerifier.create(packAndLabel.apply(OrderAcceptedMessage(orderId)))
        .expectNextMatches { it.equals(OrderDispatchedMessage(orderId)) }
        .verifyComplete()
  }

  @Test
  fun packOrder() {
    val pack: Function<OrderAcceptedMessage, Long> = catalog.lookup(Function::class.java, "pack")
    val orderId: Long = 121
    assertThat(pack.apply(OrderAcceptedMessage(orderId))).isEqualTo(orderId)
  }

  @Test
  fun labelOrder() {
    val label: Function<Flux<Long>, Flux<OrderDispatchedMessage>> =
        catalog.lookup(Function::class.java, "label")
    val orderId: Flux<Long> = Flux.just(121L)
    StepVerifier.create(label.apply(orderId))
        .expectNextMatches {
          println(it)
          it.equals(OrderDispatchedMessage(121L))
        }
        .verifyComplete()
  }
}
