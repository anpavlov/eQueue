package com.sudo.equeue.models;

import java.io.Serializable;

public class Vacancy implements Serializable {

    private int id;
    private String name;
    private String description;
    private Schedule schedule;
    private Experience experience;
    private Employment employment;
    private Area area;
    private Salary salary;
    private EmployerShort employer;

    public EmployerShort getEmployer() {
        return employer;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public Experience getExperience() {
        return experience;
    }

    public Employment getEmployment() {
        return employment;
    }

    public Area getArea() {
        return area;
    }

    public Salary getSalary() {
        return salary;
    }

    public class Schedule {

        private String id;
        private String name;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    public class Experience {

        private String id;
        private String name;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    public class Employment {

        private String id;
        private String name;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    public class Area {

        private int id;
        private String name;

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }
    }

    public class Salary {

        private Integer from;
        private Integer to;
        private String currency;

        public Integer getFrom() {
            return from;
        }

        public Integer getTo() {
            return to;
        }

        public String getCurrency() {
            return currency;
        }
    }

}
