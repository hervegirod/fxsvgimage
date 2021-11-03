package org.girod.javafx;

import java.util.concurrent.CountDownLatch;

import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import com.sun.javafx.application.PlatformImpl;

/**
 * This runner can be used to run JUnit-Tests on the JavaFx-Thread. This class can be used as a parameter to
 * the {@link RunWith} annotation. Example: *
 *
 * <pre>
 * <code>
 * &#64;RunWith( JfxTestRunner.class )
 * public class MyUnitTest
 * {
 *   &#64;Test
 *   public void testMyMethod()
 *   {
 *    //...
 *   }
 * }
 * </code>
 * </pre>
 *
 * @author okr
 * @date 18.11.2015
 *
 */
@SuppressWarnings( "restriction" )
public class JfxTestRunner extends BlockJUnit4ClassRunner
{
  /**
   * Creates a test runner, that initializes the JavaFx runtime.
   *
   * @param klass The class under test.
   * @throws InitializationError if the test class is malformed.
   */
  public JfxTestRunner( final Class<?> klass ) throws InitializationError
  {
    super( klass );
    try
    {
      setupJavaFX();
    }
    catch ( final InterruptedException e )
    {
      throw new InitializationError( "Could not initialize the JavaFx platform." );
    }
  }

  private static void setupJavaFX() throws InterruptedException
  {
    final CountDownLatch latch = new CountDownLatch( 1 );

    // initializes JavaFX environment
    PlatformImpl.startup(new Runnable() {
      @Override
      public void run() {
        /* No need to do anything here */
      }
    });

    latch.countDown();

    latch.await();
  }
}
