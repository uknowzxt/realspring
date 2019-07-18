package com.uknowzxt.test;

import com.uknowzxt.test.v3.V3AllTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import com.uknowzxt.test.v1.V1AllTests;
import com.uknowzxt.test.v2.V2AllTests;

@RunWith(Suite.class)
@SuiteClasses({V1AllTests.class,V2AllTests.class, V3AllTests.class})
public class AllTests {

}
