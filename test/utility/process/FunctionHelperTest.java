package utility.process;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by ZHENGNE on 11/7/2017.
 */
class FunctionHelperTest {

    @Test
    void isValidDate() {
        assertTrue(FunctionHelper.isValidDate("171107","yyMMdd"));
        assertFalse(FunctionHelper.isValidDate("20171107","yyMMdd"));
        assertFalse(FunctionHelper.isValidDate("171156","yyMMdd"));
        assertFalse(FunctionHelper.isValidDate("171107","yyyyMMdd"));
        assertFalse(FunctionHelper.isValidDate("20171156","yyyyMMdd"));
        assertTrue(FunctionHelper.isValidDate("20171107","yyyyMMdd"));
        assertTrue(FunctionHelper.isValidDate("0111","HHmm"));
        assertTrue(FunctionHelper.isValidDate("0111","yyMM"));
    }

}