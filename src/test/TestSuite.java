package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   SystemTest.class,
   ParserTest.class,
   StorageTest.class,
   LogicTest.class,
   UITest.class
})

public class TestSuite {   
}  