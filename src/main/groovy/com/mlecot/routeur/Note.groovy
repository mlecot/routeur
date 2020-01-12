
package com.mlecot.routeur
class Note implements Comparable<Note> {
        Integer value
        Integer velocity

        Note(Integer value) {
            this.value = value
            this.velocity=0
        }

        Note(Integer value, Integer velocity) {
            this.value = value
            this.velocity = velocity
        }

        @Override
        int compareTo(Note o) {
            return value <=> o.value
        }

        @Override
        boolean equals(Object obj) {
            return value.equals(((Note) obj).value)
        }

        @Override
        int hashCode() {
            return value.hashCode()
        }


        @Override
        public String toString() {
            return "Note{" +
                    "value=" + value +
                    '}';
        }
    }

