package com.mlecot.routeur

enum TestEnum {

        USA(3), Canada(4), Carribean(5), USPacific(6)

    TestEnum(int value) {this.value = value}
        private final int value
        public int value() {return value}

    @Override
    public String toString() {
        return "TestEnum{" +
                "value=" + value +
                '}';
    }
}


class Toto {

    TestEnum essai
    Toto(){}


}