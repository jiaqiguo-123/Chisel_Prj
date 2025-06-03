package counterchain

import chisel3._

// Declare a module definition
// There are two parameters:
//   numCounters: number of counters in chain;
//   bitwidth   : signal width of each counter;
class CounterChain(val numCounters: Int, val bitWidth: Int) extends Module {
  val io = IO(new Bundle {
    val en     = Input(Bool())
    val rst    = Input(Bool())
    val done   = Output(Bool())
    val counts = Output(Vec(numCounters, UInt(bitWidth.W)))
  })

  val maxVal   = (1 << bitWidth).U - 1.U
  // Create a register vector, which has a numCounters * bitWidth size
  val counters = RegInit(VecInit(Seq.fill(numCounters)(0.U(bitWidth.W))))
  // Create a wire net vector to pass carry signal between adjacent counters. 
  val carries  = Wire(Vec(numCounters + 1, Bool()))
  // The first carry bit is uesd to indecate a vaild counting opetation,
  // and the last one used to indecate a full load in the last counter.
  carries(0) := io.en

  // Use loops to iterate through each counter
  for (i <- 0 until numCounters) {
    when(io.rst) {                      // Reset value for each register
      counters(i) := 0.U
    } .elsewhen(carries(i)) {           // 
      when(counters(i) === maxVal) {
        counters(i) := 0.U
      } .otherwise {
        counters(i) := counters(i) + 1.U
      }
    }
  }

  // Use loops to iterate through each carriers
  for (i <- 1 until (numCounters+1)) {
    when((counters(i-1) === maxVal) && (carries(i-1) === true.B)) {
        carries(i) := true.B
    } .otherwise {
      carries(i) := false.B
    }
  }

  io.counts := counters
  // Fix the done signal so that it works when all counters reach their maximum value
  io.done := counters.forall(_ === maxVal)


  printf(p"PDS: io is $io\n")
}
