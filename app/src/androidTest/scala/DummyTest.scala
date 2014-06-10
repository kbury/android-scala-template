import org.scalatest.{Matchers, FunSpec}

class Specs extends FunSpec with Matchers {
  describe("a spec") {
    it("should do something") {
      assert(false == true);
    }
  }
}