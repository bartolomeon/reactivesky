package pl.consileon.javips;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.Test;

import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

public class TestShowcase {

    private final static String testText = "Litwo! Ojczyzno moja! ty jesteś jak zdrowie. \n" + //
            "Ile cię trzeba cenić, ten tylko się dowie, \n" + //
            "Kto cię stracił. Dziś piękność twą w całej ozdobie \n" + //
            "Widzę i opisuję, bo tęsknię po tobie."; //

    @Test
    public void testStepVerifierWithLog() {

        Flux<Integer> publisherToTest = Flux.range(5,10).log().take( 3 );

        StepVerifier.create( publisherToTest.log() ) //
                .expectSubscription() // 
                .expectNext( 5 ) //
                .expectNextCount( 2 ) //
                .verifyComplete();
    }

    
    @Test
    public void testStepVerifier() {

        Flux<String> createPublisherToTest = Flux.fromArray( testText.split( " " ) ) //
                .map( String::toUpperCase );

        StepVerifier.create( createPublisherToTest.log() ) //
                .expectSubscription() // 
                .expectNext( "LITWO!" ) //
                .expectNextCount( 3 ) //
                .expectNext( "JESTEŚ", "JAK", "ZDROWIE." ) //
                .thenConsumeWhile( Objects::nonNull ) //
                .verifyComplete();
    }

    MathContext mc = new MathContext( 2, RoundingMode.HALF_UP );

    Function<Tuple2<Integer, BigDecimal>, BigDecimal> divideThem //
            = tuple2 -> BigDecimal.valueOf( tuple2.getT1() ).divide( tuple2.getT2(), mc );

    Supplier<Flux<BigDecimal>> createPublisherDividingByZero //
            = () -> Flux.range( 1, Integer.MAX_VALUE )//
                    .zipWith( //
                            Flux.just( 2, 3, 4, 5, 0, 7, 10, 100, 500 ) //
                                    .map( BigDecimal::valueOf ) //
                    ).map( divideThem );

    @Test
    public void testStepVerifierWithError() {

        StepVerifier.create( createPublisherDividingByZero.get() ) //
                .expectNext( BigDecimal.valueOf( .5 ) ) //
                .expectNext( BigDecimal.valueOf( .67 ) ) //
                .expectNext( BigDecimal.valueOf( .75 ) ) //
                .expectNext( BigDecimal.valueOf( .8 ) ) //
                .verifyErrorMatches( //
                        t -> ArithmeticException.class.isInstance( t ) //
                                && t.getMessage().contains( "Division by zero" ) //
                );
    }

    @Test
    public void testStepVerifierWithErrorWithHooks() {

        //Hooks.onOperatorDebug();

        Flux.zip( //
                createPublisherDividingByZero.get().log().checkpoint( "Sprawdzam_1" ), //
                createPublisherDividingByZero.get().log()//
        ).subscribe();

        //and observe the stacktrace...

    }

    @Test
    public void testStepVerifierWithTime() {

        String[] words = testText.split( " " );

        Supplier<Flux<String>> createPublisherToTest = () -> Flux.fromArray( words ) //
                .map( String::toUpperCase ) //
                .take( 3 ).delayElements( Duration.ofMinutes( 1 ) ) //
                .log(); //

        StepVerifier.withVirtualTime( createPublisherToTest ) //
                .expectSubscription() //
                .expectNoEvent( Duration.ofMinutes( 1 ) ) //
                .expectNext( "LITWO!" ) //
                .thenAwait( Duration.ofMinutes( 1 ) ) //
                .expectNext( "OJCZYZNO" ) //
                .thenAwait( Duration.ofMinutes( 1 ) ) //
                .expectNext( "MOJA!" ) //
                .thenAwait( Duration.ofMinutes( 1 ) ) //
                .expectComplete().verify();
    }

    
    @Test
    public void testColdPublisher() {
        Flux<Integer> coldFlux = Flux.range( 1, 3 ).map( v -> v * 2 );

        coldFlux.subscribe( System.out::println );
        coldFlux.subscribe( System.out::println );

        coldFlux.blockLast();
    }

    
    @Test
    public void testHotPublisher() {
        DirectProcessor<Integer> hotSrc = DirectProcessor.create();
        Flux<Integer> hotFlux = hotSrc.map(v -> v*2);
        
        hotFlux.subscribe(System.out::println);
        hotSrc.onNext( 1 );
        hotFlux.subscribe(System.out::println);
        hotSrc.onNext( 2 );
        
    }

    
    @Test
    public void testBroadcastingPublisher() throws InterruptedException {
        ConnectableFlux<Integer> connectableFlux = Flux.range(1,3).publish();
        Flux<Integer> publisher = connectableFlux.autoConnect(2);

        publisher.subscribe(v -> System.out.println("subscriber1 - " + v));
        publisher.subscribe(v -> System.out.println("subscriber2 - " + v));
        
    }
    
    @Test
    public void testAutoConnect() throws InterruptedException {
        Flux<Integer> source = Flux.range(1, 3)
                .doOnSubscribe(s -> System.out.println("subscribed to source"));
        
        Flux<Integer> autoCo = source.publish().autoConnect(2);
        
        autoCo.subscribe(System.out::println, e -> {}, () -> {});
        System.out.println("subscribed first");
        Thread.sleep(500);
        System.out.println("subscribing second");
        autoCo.subscribe(System.out::println, e -> {}, () -> {});
    }

}
