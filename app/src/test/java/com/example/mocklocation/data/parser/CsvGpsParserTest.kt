package com.example.mocklocation.data.parser

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("CsvGpsParser 테스트")
class CsvGpsParserTest {
    private lateinit var parser: CsvGpsParser

    @BeforeEach
    fun setup() {
        parser = CsvGpsParser()
    }

    @Test
    @DisplayName("parseLine: CSV 한 줄을 파싱해서 GpsPoint 객체로 변환")
    fun parse() {
        val line = "713644 2025/06/24 14:33:56.468559 5467.3430 72 IDCE ALD LCAT 930 log info verbose 1 #onLocationChanged[5683]:isSensorMode false : accuracy:0.6285398, satelliteCount:40, speed:0.014438872, bearing:254.5473, provider:gps, time: 1750743260694 Location[gps 35.158274,129.027484 hAcc=0.6285398 et=+1h31m7s320ms alt=73.45844268798828 vAcc=1.0635567 vel=0.014438872 sAcc=0.37752575 bear=254.5473 bAcc=2.086191 {Bundle[{satellites=20, maxCn0=45, meanCn0=20}]}]"

        val result = parser.parseLine(line)

        assertEquals(1750743260694L, result?.timestamp)
        assertEquals(35.158274, result?.latitude)
        assertEquals(129.027484, result?.longitude)
        assertEquals(0.6285398f, result?.accuracy)
        assertEquals(0.014438872f, result?.speed)
        assertEquals(254.5473f, result?.bearing)
        assertEquals(20, result?.satellites)
    }

    @Test
    @DisplayName("parseLine: 올바르지 않은 줄은 null 반환")
    fun parseLineInvalidTest() {
        val invalidLine = "invalid data line without gps info"

        val result = parser.parseLine(invalidLine)

        assertNull(result)
    }

    @Test
    @DisplayName("parse: InputStream에서 GPS 데이터만 추출")
    fun parseFileTest() {
        val tempFile = createTempFile().apply {
            writeText(
                """
                721557 2025/06/24 14:33:57.564789 5468.4410 188 IDCE ALD LCAT 930 log info verbose 1 #onLocationChanged[5683]:isSensorMode false : accuracy:0.6272337, satelliteCount:40, speed:0.029629089, bearing:254.5032, provider:gps, time: 1750743261804 Location[gps 35.158273,129.027485 hAcc=0.6272337 et=+1h31m8s410ms alt=73.6487808227539 vAcc=1.0602552 vel=0.029629089 sAcc=0.38269743 bear=254.5032 bAcc=2.086191 {Bundle[{satellites=20, maxCn0=46, meanCn0=20}]}]
                728848 2025/06/24 14:33:58.608278 5469.4844 204 IDCE ALD LCAT 930 log info verbose 1 #onLocationChanged[5683]:isSensorMode false : accuracy:0.6268064, satelliteCount:40, speed:0.026479315, bearing:252.62878, provider:gps, time: 1750743262804 Location[gps 35.158272,129.027485 hAcc=0.6268064 et=+1h31m9s417ms alt=73.7088623046875 vAcc=1.059966 vel=0.026479315 sAcc=0.38278857 bear=252.62878 bAcc=2.086191 {Bundle[{satellites=20, maxCn0=46, meanCn0=20}]}]
                721559 2025/06/24 14:33:57.564911 5468.4412 190 IDCE ALD LCAT 930 log info verbose 1 #GPS_LOG[5683]:129.027484636391,35.15827310737617,254.5032,0.029629089,1750743261,40,NONE,0.6272337,0,129.02748396297832,35.15827408918207,254,20250624_143402,73.6487808227539,0,gps,0,1750743261804,0,1750743242267
                721560 2025/06/24 14:33:57.564918 5468.4412 191 IDCE ALD LCAT 930 log info verbose 1 mcipcsvr[1133]:I: [client_send] 2025-06-24 14:34:02.264443 1133 Can't find client 'DSP0_CLI_AC_IVI' on core 6
                """
            )
        }
        val inputStream = tempFile.inputStream()

        val result = parser.parse(inputStream)

        assertEquals(2, result.size)
        assertEquals(35.158273, result[0].latitude)
        assertEquals(129.027485, result[0].longitude)
        assertEquals(35.158272, result[1].latitude)
        assertEquals(129.027485, result[1].longitude)

        tempFile.deleteOnExit()
    }

    @Test
    @DisplayName("findValues: regex로 문자열에서 값 추출")
    fun findValuesTest() {
        val line = "728848 2025/06/24 14:33:58.608278 5469.4844 204 IDCE ALD LCAT 930 log info verbose 1 #onLocationChanged[5683]:isSensorMode false : accuracy:0.6268064, satelliteCount:40, speed:0.026479315, bearing:252.62878, provider:gps, time: 1750743262804 Location[gps 35.158272,129.027485 hAcc=0.6268064 et=+1h31m9s417ms alt=73.7088623046875 vAcc=1.059966 vel=0.026479315 sAcc=0.38278857 bear=252.62878 bAcc=2.086191 {Bundle[{satellites=20, maxCn0=46, meanCn0=20}]}]"
        val expected = listOf("1750743262804", "35.158272", "129.027485", "0.6268064", "0.026479315", "252.62878", "20")

        val result = parser.findValues(line, CsvGpsParser.DEFAULT_REGEX)

        assertEquals(expected, result)
    }
}