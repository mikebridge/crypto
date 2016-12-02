import com.bridgecanada.utils.HexConversions._

//for (a <- 1 until 1000 if (1 - 7 * a) % 23 == 0)
//  yield (a, (1 - 7 * a) / 23 )

for (x <- 1 until 100 if (3 * x + 2) % 19 == 0)
  yield x
