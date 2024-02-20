import sys
from dataclasses import dataclass
from pathlib import Path


@dataclass
class CONSTANT_Utf8_info:
    tag: int
    length: int
    arr: bytes


@dataclass
class CONSTANT_Integer_info:
    tag: int
    data: int


class ClassFile:
    def __init__(self, arr: bytes) -> None:
        self.arr = arr
        self.idx = 0
        self.parse()

    def parse(self):
        self.parse_magic()
        self.parse_minor()
        self.parse_major()
        self.parse_cp_count()
        self.parse_cp()
        self.parse_acc_flags()
        self.parse_thisclass()
        self.parse_superclass()
        self.parse_ifc_count()
        self.parse_ifcs()
        self.parse_fields_count()
        self.parse_fields()
        self.parse_methods_count()
        self.parse_methods()
        self.parse_attributes_count()
        self.parse_attributes()

    def parse_magic(self):
        self.magic = self.arr[:4]
        self.idx += 4

    def parse_minor(self):
        self.minor_version = int.from_bytes(
            self.arr[self.idx : self.idx + 2], signed=True
        )
        self.idx += 2

    def parse_major(self):
        self.major_version = int.from_bytes(
            self.arr[self.idx : self.idx + 2], signed=True
        )
        self.idx += 2

    def parse_cp_count(self):
        self.cp_count = int.from_bytes(self.arr[self.idx : self.idx + 2], signed=True)
        self.idx += 2

    def parse_cp(self):
        print("TODO parse constant pool")
        self.constant_pool = []
        for _ in range(self.cp_count):
            # parse cp_info, indexed from 1 to count-1 (why from 1?)
            self._parse_cp_entry()

    def _parse_cp_entry(self):
        tag = int.from_bytes(self.arr[self.idx : self.idx + 1])
        self.idx += 1
        print("Tag:", tag)
        print("TODO parse constant pool entry")
        match tag:
            case 1:
                self._parse_cp_utf8()
            case 3:
                self._parse_cp_integer()
            case 4:
                self._parse_cp_float()
            case 5:
                self._parse_cp_long()
            case 6:
                self._parse_cp_double()
            case 7:
                self._parse_cp_class()
            case 8:
                self._parse_cp_string()
            case 9:
                self._parse_cp_fieldref()
            case 10:
                self._parse_cp_methodref()
            case 11:
                self._parse_cp_ifcmethodref()
            case 12:
                self._parse_cp_nameandtype()
            case 15:
                self._parse_cp_methodhandle()
            case 16:
                self._parse_cp_methodtype()
            case 18:
                self._parse_cp_invokedynamic()
            case _:
                print(f"Unknown tag: {tag}")

    def _parse_cp_utf8(self):
        self.idx += 1  # skip tag, already read
        len = int.from_bytes(self.arr[self.idx : self.idx + 2])
        self.idx += 2

        self.constant_pool.append(
            CONSTANT_Utf8_info(1, len, self.arr[self.idx : self.idx + len])
        )

    def _parse_cp_integer(self):
        self.idx += 1  # skip tag, already read
        data = int.from_bytes(self.arr[self.idx : self.idx + 4])
        self.idx += 4

        self.constant_pool.append(CONSTANT_Integer_info(3, data))

    def parse_acc_flags(self):
        print("TODO: parse access flags")

    def parse_thisclass(self):
        print("TODO: parse this class ref")

    def parse_superclass(self):
        print("TODO: parse superclass ref")

    def parse_ifc_count(self):
        self.ifc_count = int.from_bytes(self.arr[self.idx : self.idx + 2], signed=True)
        self.idx += 2

    def parse_ifcs(self):
        print("TODO: parse iterfaces")

    def parse_fields_count(self):
        self.fields_count = int.from_bytes(
            self.arr[self.idx : self.idx + 2], signed=True
        )
        self.idx += 2

    def parse_fields(self):
        print("TODO: parse fields")

    def parse_methods_count(self):
        self.methods_count = int.from_bytes(
            self.arr[self.idx : self.idx + 2], signed=True
        )
        self.idx += 2

    def parse_methods(self):
        print("TODO: parse methods")

    def parse_attributes_count(self):
        self.attributes_count = int.from_bytes(
            self.arr[self.idx : self.idx + 2], signed=True
        )
        self.idx += 2

    def parse_attributes(self):
        print("TODO: parse attributes")

    def pretty_print(self):
        print("Magic bytes:", self.magic.hex())
        print("Minor version:", self.minor_version)
        print("Minor version:", self.major_version)


if len(sys.argv) != 2:
    print(f"Usage: {sys.argv[0]} <classfile>")
    exit(1)

classfile = Path(sys.argv[1])
classfile_bytes = classfile.read_bytes()
cf = ClassFile(classfile_bytes)
cf.pretty_print()
