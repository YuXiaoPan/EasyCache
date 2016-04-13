package li.allan.utils;

import java.util.Calendar;

public class TimeUtil {
	static class CalendarWrapper {
		Calendar cal;

		public boolean isAfter(final CalendarWrapper dateWrapper) {
			return cal.after(dateWrapper.getCal());
		}

		public boolean isBefore(final CalendarWrapper dateWrapper) {
			return cal.before(dateWrapper.getCal());
		}

		public CalendarWrapper addSecond(final int second) {
			cal.add(Calendar.SECOND, second);
			return this;
		}

		public CalendarWrapper addMinute(final int minute) {
			cal.add(Calendar.MINUTE, minute);
			return this;
		}

		public CalendarWrapper addHour(final int hour) {
			cal.add(Calendar.HOUR, hour);
			return this;
		}

		public CalendarWrapper addDay(final int day) {
			cal.add(Calendar.DATE, day);
			return this;
		}

		public CalendarWrapper addMonth(final int month) {
			cal.add(Calendar.MONTH, month);
			return this;
		}

		public int diffTime(final CalendarWrapper calendarWrapper) {
			return TimeUtil.diffTime(this, calendarWrapper);
		}

		private Calendar getCal() {
			return cal;
		}

		public CalendarWrapper(Calendar cal) {
			this.cal = cal;
		}

		@Override
		public String toString() {
			return "CalendarWrapper{" +
					"cal=" + cal +
					'}';
		}
	}

	public static CalendarWrapper now() {
		Calendar cal = Calendar.getInstance();
		return new CalendarWrapper(cal);
	}

	public static CalendarWrapper today() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new CalendarWrapper(cal);
	}

	public static CalendarWrapper tomorrow() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new CalendarWrapper(cal);
	}

	public static int diffTime(final CalendarWrapper c1, final CalendarWrapper c2) {
		long diffTime = c1.getCal().getTimeInMillis() - c2.getCal().getTimeInMillis();
		return (diffTime > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (diffTime > 0 ? (int) (diffTime/1000) : 0);
	}
}
