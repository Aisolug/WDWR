mu <- c(45, 35, 40)
sigma <- matrix(c(1, -2, -1, -2, 36, -8, -1, -8, 9), nrow=3, ncol=3)

data <- rtmvt(n=10000, mean=mu, sigma=sigma, df = 4, lower = c(20,20,20), upper=c(50,50,50))

data

write.table(data, "d:/R/data10000.txt", sep="\t", col.names = F, row.names = F)

Er1 <- c(45 + 1*(gamma(3/2)*((4+(-25)^2)^(-3/2)-(4+5^2)^(-3/2))*4^2)/(2*(pt(5,4)-pt(-25,4))*gamma(2)*gamma(1/2)))
Er1

Er2 <- c(35 + 2*(gamma(3/2)*((4+(-5/2)^2)^(-3/2)-(4+(15/2)^2)^(-3/2))*4^2)/(2*(pt(15/2,4)-pt(-5/2,4))*gamma(2)*gamma(1/2)))
Er2

Er3 <- c(40 + 3*(gamma(3/2)*((4+(-20/3)^2)^(-3/2)-(4+(10/3)^2)^(-3/2))*4^2)/(2*(pt(10/3,4)-pt(-20/3,4))*gamma(2)*gamma(1/2)))
Er3

write.table(Er1, "d:/R/er1.txt", sep="\t", col.names = F, row.names = F)
write.table(Er2, "d:/R/er2.txt", sep="\t", col.names = F, row.names = F)
write.table(Er3, "d:/R/er3.txt", sep="\t", col.names = F, row.names = F)

