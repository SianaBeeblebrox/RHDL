<img src="RHDL_transparent.png" alt="RHDL: Redstone Hardware Description Language" width="500"/><br/>
[![GitHub](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)](https://github.com/SianaBeeblebrox/RHDL) [![LicenseRef-NYA-1.0](https://img.shields.io/badge/License-NYA--1.0-44bb00?style=for-the-badge)](https://github.com/SianaBeeblebrox/RHDL/blob/main/LICENSE) [![GitHub last commit](https://img.shields.io/github/last-commit/SianaBeeblebrox/RHDL?style=for-the-badge)](https://github.com/SianaBeeblebrox/Specter/commits/main/)
---
Redstone Hardware Description Language
## Comments
`# ...`
## Operators
+ Logical AND (`a & b`)
+ Logical OR (`a | b`)
+ Logical XOR (`a ^ b`)
+ Logical NOT (`~a`)
+ Concatenation (`a <> b`)
+ Slicing (`a[i:j]`)
+ Ternary (`a ? b : c`)
## Switches
```
switch a
  case b: c
  ...
  case: d
end
```
## Functions
+ `any(a)`
+ `all(a)`
+ `inc(a)`
+ `dec(a)`
+ `zero(n)`
+ `one(n)`
+ `rand(n)`
+ `print(...)`
