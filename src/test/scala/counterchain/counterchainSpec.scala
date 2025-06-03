package counterchain

//import chisel3._
//import chisel3.util._
//import chiseltest._
//import org.scalatest.FreeSpec
//import chisel3.stage.ChiselStage
import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.scalatest.ChiselSim
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

// 引入你定义的 CounterChain 模块
class CounterChainSpec extends AnyFreeSpec with Matchers with ChiselSim {
  "CounterChain should count and chain properly" in {
    simulate(new CounterChain(numCounters = 2, bitWidth = 2)) { dut =>
      // 启用 printf 输出
      dut.io.en.poke(false.B)
      dut.io.rst.poke(true.B)
      dut.clock.step()
      dut.io.en.poke(true.B)
      dut.io.rst.poke(false.B)

      for (cycle <- 0 to 25) {
        if (cycle == 3) {
          // 第5个周期触发复位
          dut.io.rst.poke(true.B)
        } else if (cycle == 6) {
          // 第6个周期释放复位
          dut.io.rst.poke(false.B)
        }

        // 步进一个时钟周期
        dut.clock.step()

        // 每个周期打印当前计数器状态和 done 信号
        println(s"[Cycle $cycle]\n")
        //println(s" Counters: ${dut.io.counts.debugString()}\n")
        //println(s" Done    : ${dut.io.done.litValue}\n")
      }

      // 最终检查 done 是否在某个时刻变为 true
      // 可以结合日志观察哪个 cycle 达到了最大值
    }
  }
}
