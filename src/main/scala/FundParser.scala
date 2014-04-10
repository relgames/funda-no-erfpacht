import org.openqa.selenium.By
import org.openqa.selenium.htmlunit.HtmlUnitDriver

/**
 * @author opoleshuk
 */
object FundParser {
  val FUNDA_URL = "http://m.funda.nl/koop/amsterdam/200000-375000/85+woonopp/"

  def main(args: Array[String]) {
    val driver = new HtmlUnitDriver
    driver.get(FUNDA_URL)
    val links = driver.findElements(By.tagName("a"))
  }
}
